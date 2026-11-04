package com.aibasedtyping.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "corrections")
data class CorrectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalWord: String,
    val correctedTo: String,
    val count: Int = 1,
    val lastOccurred: Long = System.currentTimeMillis()
)
