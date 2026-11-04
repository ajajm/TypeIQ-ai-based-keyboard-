package com.aibasedtyping.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "typing_behaviour")
data class TypingBehaviourEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bigramFirst: String,
    val bigramSecond: String,
    val frequency: Double = 1.0,
    val lastUsed: Long = System.currentTimeMillis(),
    val timeBucket: Int = 0 // 0=morning(6-12), 1=afternoon(12-17), 2=evening(17-22), 3=night(22-6)
)
