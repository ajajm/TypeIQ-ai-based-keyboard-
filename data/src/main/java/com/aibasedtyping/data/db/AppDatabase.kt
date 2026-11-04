package com.aibasedtyping.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserDictionaryEntity::class, TypingBehaviourEntity::class, CorrectionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDictionaryDao(): UserDictionaryDao
    abstract fun typingBehaviourDao(): TypingBehaviourDao
    abstract fun correctionDao(): CorrectionDao
}
