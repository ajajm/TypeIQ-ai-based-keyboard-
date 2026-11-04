package com.aibasedtyping.prediction.lm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MiniLMEngine(
    private val modelFile: File? = null
) {
    private var isLoaded = false

    suspend fun loadModel(): Boolean = withContext(Dispatchers.IO) {
        if (modelFile != null && modelFile.exists()) {
            isLoaded = true
            true
        } else {
            false
        }
    }

    suspend fun generateCompletion(
        prompt: String,
        maxTokens: Int = 15,
        temperature: Float = 0.7f
    ): String? = withContext(Dispatchers.Default) {
        if (!isLoaded) {
            // Heuristic fallback for offline sentence completions when model file is downloading or not yet loaded
            return@withContext generateHeuristicSentence(prompt)
        }

        // Native llama.cpp / NDK call would execute here when GGUF is mounted
        generateHeuristicSentence(prompt)
    }

    private fun generateHeuristicSentence(context: String): String? {
        val trimmed = context.trim().lowercase()
        return when {
            trimmed.endsWith("how are") -> "you doing today?"
            trimmed.endsWith("thank you") -> "so much for your help."
            trimmed.endsWith("let me") -> "know when you are available."
            trimmed.endsWith("looking forward") -> "to seeing you soon."
            trimmed.endsWith("sounds") -> "like a great plan!"
            trimmed.endsWith("please let") -> "me know if you need anything else."
            trimmed.endsWith("hope you") -> "are having a wonderful day."
            trimmed.endsWith("see you") -> "tomorrow at the office."
            else -> null
        }
    }

    fun isModelReady(): Boolean = isLoaded
}

