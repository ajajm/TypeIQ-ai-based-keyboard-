package com.aibasedtyping.data.repository

import com.aibasedtyping.data.db.UserDictionaryDao
import com.aibasedtyping.data.db.UserDictionaryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDictRepository @Inject constructor(
    private val dao: UserDictionaryDao
) {
    private val decayFactor = 0.998

    suspend fun learnWord(word: String) {
        val existing = dao.getWord(word.lowercase())
        if (existing != null) {
            dao.insertOrUpdate(
                existing.copy(
                    frequency = existing.frequency + 1.0,
                    lastUsed = System.currentTimeMillis()
                )
            )
        } else {
            dao.insertOrUpdate(
                UserDictionaryEntity(
                    word = word.lowercase(),
                    frequency = 1.0,
                    lastUsed = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun getBoost(word: String): Double {
        return dao.getWord(word.lowercase())?.frequency ?: 0.0
    }

    suspend fun blockWord(word: String) {
        dao.blockWord(word.lowercase())
    }

    suspend fun applySessionDecay() {
        val oneDayAgo = System.currentTimeMillis() - 86_400_000L
        dao.applyDecay(decayFactor, oneDayAgo)
        dao.pruneRareWords(0.01)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    suspend fun exportAll(): List<UserDictionaryEntity> {
        return dao.exportAll()
    }

    suspend fun importWords(words: List<UserDictionaryEntity>) {
        words.forEach { dao.insertOrUpdate(it) }
    }
}
