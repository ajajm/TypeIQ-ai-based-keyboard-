package com.aibasedtyping.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_dictionary")
data class UserDictionaryEntity(
    @PrimaryKey val word: String,
    val frequency: Double = 1.0,
    val lastUsed: Long = System.currentTimeMillis(),
    val isBlocked: Boolean = false
)
