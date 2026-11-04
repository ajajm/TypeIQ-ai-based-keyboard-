package com.aibasedtyping.data.di

import android.content.Context
import androidx.room.Room
import com.aibasedtyping.data.db.AppDatabase
import com.aibasedtyping.data.db.CorrectionDao
import com.aibasedtyping.data.db.TypingBehaviourDao
import com.aibasedtyping.data.db.UserDictionaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ai_typing_db"
        ).build()
    }

    @Provides
    fun provideUserDictionaryDao(db: AppDatabase): UserDictionaryDao = db.userDictionaryDao()

    @Provides
    fun provideTypingBehaviourDao(db: AppDatabase): TypingBehaviourDao = db.typingBehaviourDao()

    @Provides
    fun provideCorrectionDao(db: AppDatabase): CorrectionDao = db.correctionDao()
}
