package com.aibasedtyping.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDictionaryDao {
    @Query("SELECT * FROM user_dictionary WHERE isBlocked = 0 ORDER BY frequency DESC")
    suspend fun getAllWords(): List<UserDictionaryEntity>

    @Query("SELECT * FROM user_dictionary WHERE word = :word")
    suspend fun getWord(word: String): UserDictionaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: UserDictionaryEntity)

    @Query("UPDATE user_dictionary SET frequency = frequency * :decayFactor WHERE lastUsed < :beforeTimestamp")
    suspend fun applyDecay(decayFactor: Double, beforeTimestamp: Long)

    @Query("UPDATE user_dictionary SET isBlocked = 1 WHERE word = :word")
    suspend fun blockWord(word: String)

    @Query("DELETE FROM user_dictionary WHERE frequency < :minFrequency AND isBlocked = 0")
    suspend fun pruneRareWords(minFrequency: Double)

    @Query("DELETE FROM user_dictionary")
    suspend fun clearAll()

    @Query("SELECT * FROM user_dictionary WHERE isBlocked = 0")
    suspend fun exportAll(): List<UserDictionaryEntity>
}
