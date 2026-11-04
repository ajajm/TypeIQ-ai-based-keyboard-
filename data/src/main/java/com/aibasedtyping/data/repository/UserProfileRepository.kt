package com.aibasedtyping.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aibasedtyping.data.model.Language
import com.aibasedtyping.data.model.ModelSize
import com.aibasedtyping.data.model.Profession
import com.aibasedtyping.data.model.TypingStyle
import com.aibasedtyping.data.model.Usage
import com.aibasedtyping.data.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "user_profile")

    companion object {
        val KEY_LANGUAGES = stringPreferencesKey("languages") // comma-separated codes
        val KEY_TYPING_STYLE = stringPreferencesKey("typing_style")
        val KEY_USAGE = stringPreferencesKey("usage")
        val KEY_PROFESSION = stringPreferencesKey("profession")
        val KEY_SENTENCE_ENABLED = booleanPreferencesKey("sentence_prediction_enabled")
        val KEY_MODEL_SIZE = stringPreferencesKey("model_size")
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            languages = prefs[KEY_LANGUAGES]?.split(",")?.mapNotNull { code ->
                Language.entries.find { it.code == code }
            } ?: listOf(Language.ENGLISH),
            typingStyle = prefs[KEY_TYPING_STYLE]?.let { name ->
                TypingStyle.entries.find { it.name == name }
            } ?: TypingStyle.MIXED,
            usage = prefs[KEY_USAGE]?.let { name ->
                Usage.entries.find { it.name == name }
            } ?: Usage.BOTH,
            profession = prefs[KEY_PROFESSION]?.let { name ->
                Profession.entries.find { it.name == name }
            } ?: Profession.OTHER,
            sentencePredictionEnabled = prefs[KEY_SENTENCE_ENABLED] ?: true,
            modelSize = prefs[KEY_MODEL_SIZE]?.let { name ->
                ModelSize.entries.find { it.name == name }
            } ?: ModelSize.SMALL,
            onboardingCompleted = prefs[KEY_ONBOARDING_DONE] ?: false
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGES] = profile.languages.joinToString(",") { it.code }
            prefs[KEY_TYPING_STYLE] = profile.typingStyle.name
            prefs[KEY_USAGE] = profile.usage.name
            prefs[KEY_PROFESSION] = profile.profession.name
            prefs[KEY_SENTENCE_ENABLED] = profile.sentencePredictionEnabled
            prefs[KEY_MODEL_SIZE] = profile.modelSize.name
            prefs[KEY_ONBOARDING_DONE] = profile.onboardingCompleted
        }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { it.clear() }
    }
}
