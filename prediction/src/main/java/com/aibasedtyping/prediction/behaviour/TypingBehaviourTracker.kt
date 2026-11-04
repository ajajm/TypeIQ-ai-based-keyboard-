package com.aibasedtyping.prediction.behaviour

import com.aibasedtyping.data.db.TypingBehaviourDao
import com.aibasedtyping.data.db.TypingBehaviourEntity
import com.aibasedtyping.data.repository.UserDictRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class TypingBehaviourTracker(
    private val behaviourDao: TypingBehaviourDao,
    private val userDictRepo: UserDictRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var lastKeyTimestamp: Long = System.currentTimeMillis()
    private var lastWord: String? = null

    // Formality tracking: running count of formal vs casual indicators
    private var formalWordCount = 0
    private var casualWordCount = 0

    private val casualTokens = setOf("u", "ur", "r", "idk", "tbh", "omg", "lol", "haha", "k", "thx", "pls")
    private val formalTokens = setOf("furthermore", "regarding", "sincerely", "therefore", "however", "additionally")

    fun onKeystroke() {
        lastKeyTimestamp = System.currentTimeMillis()
    }

    fun getTimeSinceLastKeystrokeMs(): Long {
        return System.currentTimeMillis() - lastKeyTimestamp
    }

    fun onWordCommitted(word: String, isIncognito: Boolean) {
        if (isIncognito || word.isBlank()) return

        val cleanWord = word.lowercase().trim()

        // Track formality
        if (casualTokens.contains(cleanWord)) casualWordCount++
        if (formalTokens.contains(cleanWord)) formalWordCount++

        scope.launch {
            // Learn word in user dictionary
            userDictRepo.learnWord(cleanWord)

            // Track bigram pattern
            lastWord?.let { prev ->
                val bucket = getTimeBucket()
                val existing = behaviourDao.getBigram(prev, cleanWord, bucket)
                if (existing != null) {
                    behaviourDao.insertOrUpdate(
                        existing.copy(
                            frequency = existing.frequency + 1.0,
                            lastUsed = System.currentTimeMillis()
                        )
                    )
                } else {
                    behaviourDao.insertOrUpdate(
                        TypingBehaviourEntity(
                            bigramFirst = prev,
                            bigramSecond = cleanWord,
                            frequency = 1.0,
                            timeBucket = bucket
                        )
                    )
                }
            }
            lastWord = cleanWord
        }
    }

    fun getTimeBucket(): Int {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..11 -> 0  // Morning
            in 12..16 -> 1 // Afternoon
            in 17..21 -> 2 // Evening
            else -> 3      // Night
        }
    }

    fun getFormalityScore(): Float {
        val total = formalWordCount + casualWordCount
        if (total == 0) return 0.5f // Neutral
        return formalWordCount.toFloat() / total.toFloat()
    }

    fun resetSession() {
        lastWord = null
        lastKeyTimestamp = System.currentTimeMillis()
    }
}

