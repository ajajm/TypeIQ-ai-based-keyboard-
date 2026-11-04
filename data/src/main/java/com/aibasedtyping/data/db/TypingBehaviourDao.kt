package com.aibasedtyping.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TypingBehaviourDao {
    @Query("SELECT * FROM typing_behaviour WHERE bigramFirst = :first ORDER BY frequency DESC LIMIT :limit")
    suspend fun getBigramsFor(first: String, limit: Int = 10): List<TypingBehaviourEntity>

    @Query("SELECT * FROM typing_behaviour WHERE bigramFirst = :first AND timeBucket = :bucket ORDER BY frequency DESC LIMIT :limit")
    suspend fun getBigramsForTimeBucket(first: String, bucket: Int, limit: Int = 10): List<TypingBehaviourEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: TypingBehaviourEntity)

    @Query("SELECT * FROM typing_behaviour WHERE bigramFirst = :first AND bigramSecond = :second AND timeBucket = :bucket")
    suspend fun getBigram(first: String, second: String, bucket: Int): TypingBehaviourEntity?

    @Query("UPDATE typing_behaviour SET frequency = frequency * :decayFactor")
    suspend fun applyDecayAll(decayFactor: Double)

    @Query("DELETE FROM typing_behaviour WHERE frequency < :minFrequency")
    suspend fun pruneRare(minFrequency: Double)

    @Query("DELETE FROM typing_behaviour")
    suspend fun clearAll()
}
