package com.aibasedtyping.prediction.ngram

class NgramModel {
    // Bigram map: word1 -> (word2 -> frequency)
    private val bigrams = HashMap<String, HashMap<String, Double>>()

    // Trigram map: "word1 word2" -> (word3 -> frequency)
    private val trigrams = HashMap<String, HashMap<String, Double>>()

    fun addBigram(w1: String, w2: String, frequency: Double = 1.0) {
        val nextMap = bigrams.getOrPut(w1.lowercase()) { HashMap() }
        nextMap[w2.lowercase()] = (nextMap[w2.lowercase()] ?: 0.0) + frequency
    }

    fun addTrigram(w1: String, w2: String, w3: String, frequency: Double = 1.0) {
        val key = "${w1.lowercase()} ${w2.lowercase()}"
        val nextMap = trigrams.getOrPut(key) { HashMap() }
        nextMap[w3.lowercase()] = (nextMap[w3.lowercase()] ?: 0.0) + frequency
    }

    fun predictNextWords(contextWords: List<String>, limit: Int = 20): List<Pair<String, Double>> {
        val cleaned = contextWords.map { it.lowercase().trim() }.filter { it.isNotEmpty() }
        if (cleaned.isEmpty()) return emptyList()

        val results = HashMap<String, Double>()

        // Trigram prediction if at least 2 words of context
        if (cleaned.size >= 2) {
            val key = "${cleaned[cleaned.size - 2]} ${cleaned.last()}"
            trigrams[key]?.forEach { (w, freq) ->
                results[w] = (results[w] ?: 0.0) + (freq * 2.0) // Trigram gets 2x weight
            }
        }

        // Bigram prediction using last word
        val lastWord = cleaned.last()
        bigrams[lastWord]?.forEach { (w, freq) ->
            results[w] = (results[w] ?: 0.0) + freq
        }

        return results.entries
            .map { Pair(it.key, it.value) }
            .sortedByDescending { it.second }
            .take(limit)
    }

    fun clear() {
        bigrams.clear()
        trigrams.clear()
    }
}

