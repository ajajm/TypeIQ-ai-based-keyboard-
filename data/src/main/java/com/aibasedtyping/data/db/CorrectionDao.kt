package com.aibasedtyping.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CorrectionDao {
    @Query("SELECT * FROM corrections WHERE originalWord = :word ORDER BY count DESC LIMIT 5")
    suspend fun getCorrectionsFor(word: String): List<CorrectionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: CorrectionEntity)

    @Query("SELECT * FROM corrections WHERE originalWord = :original AND correctedTo = :corrected")
    suspend fun getCorrection(original: String, corrected: String): CorrectionEntity?

    @Query("DELETE FROM corrections")
    suspend fun clearAll()
}
