package com.aibasedtyping.prediction.lm

import com.aibasedtyping.data.model.UserProfile
import com.aibasedtyping.prediction.behaviour.TypingBehaviourTracker

class SentencePredictor(
    private val lmMiniEngine: MiniLMEngine,
    private val behaviourTracker: TypingBehaviourTracker? = null
) {
    suspend fun predictSentence(
        textBeforeCursor: String,
        userProfile: UserProfile?
    ): String? {
        if (userProfile?.sentencePredictionEnabled == false) return null
        if (textBeforeCursor.length < 5) return null

        // Check pause threshold (e.g., > 1.2s pause) or word boundary
        val timeSinceLastKey = behaviourTracker?.getTimeSinceLastKeystrokeMs() ?: 0L
        val isPause = timeSinceLastKey > 1200L
        val endsWithSpace = textBeforeCursor.endsWith(" ")

        if (!endsWithSpace && !isPause) {
            return null
        }

        val prompt = textBeforeCursor.takeLast(150)
        return lmMiniEngine.generateCompletion(prompt)
    }
}

