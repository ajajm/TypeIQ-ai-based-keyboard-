package com.aibasedtyping.prediction

import com.aibasedtyping.data.repository.UserDictRepository
import com.aibasedtyping.prediction.behaviour.TypingBehaviourTracker
import com.aibasedtyping.prediction.langpack.EnglishLanguagePack
import com.aibasedtyping.prediction.langpack.LanguagePack
import com.aibasedtyping.prediction.ngram.NgramModel
import com.aibasedtyping.prediction.trie.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CandidateEngine(
    private val userDictRepo: UserDictRepository? = null,
    private val behaviourTracker: TypingBehaviourTracker? = null,
    languagePack: LanguagePack = EnglishLanguagePack()
) {
    private val trie = Trie()
    private val ngramModel = NgramModel()

    init {
        languagePack.populateTrie(trie)
        languagePack.populateNgramModel(ngramModel)
    }

    suspend fun getCandidates(
        textBeforeCursor: String,
        limit: Int = 20
    ): List<String> = withContext(Dispatchers.Default) {
        val trimmed = textBeforeCursor.trimEnd('\r', '\n')
        if (trimmed.isEmpty()) return@withContext emptyList()

        val endsWithSpace = textBeforeCursor.endsWith(" ")
        val tokens = trimmed.split(Regex("\\s+")).filter { it.isNotEmpty() }

        val candidateScores = HashMap<String, Double>()

        if (endsWithSpace || tokens.isEmpty()) {
            // Next-word prediction via N-gram
            val ngramResults = ngramModel.predictNextWords(tokens, limit)
            ngramResults.forEach { (word, score) ->
                candidateScores[word] = score
            }
        } else {
            // In-word completion via Trie
            val currentPrefix = tokens.last()
            val trieResults = trie.searchPrefix(currentPrefix, limit)
            trieResults.forEach { (word, score) ->
                candidateScores[word] = score
            }

            // Also check if n-gram has predictions matching the prefix
            if (tokens.size >= 2) {
                val context = tokens.dropLast(1)
                val ngramResults = ngramModel.predictNextWords(context, limit)
                ngramResults.filter { it.first.startsWith(currentPrefix.lowercase()) }.forEach { (word, score) ->
                    candidateScores[word] = (candidateScores[word] ?: 0.0) + (score * 1.5)
                }
            }
        }

        // Apply personal dictionary boost
        userDictRepo?.let { repo ->
            candidateScores.keys.toList().forEach { word ->
                val boost = repo.getBoost(word)
                if (boost > 0.0) {
                    candidateScores[word] = (candidateScores[word] ?: 1.0) * (1.0 + (boost * 0.2))
                }
            }
        }

        candidateScores.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }
}

