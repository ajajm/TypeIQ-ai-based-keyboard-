package com.aibasedtyping.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aibasedtyping.app.onboarding.OnboardingActivity
import com.aibasedtyping.data.repository.SettingsRepository
import com.aibasedtyping.data.repository.UserDictRepository
import com.aibasedtyping.data.repository.UserProfileRepository
import com.aibasedtyping.settings.DictManagerScreen
import com.aibasedtyping.settings.PersonalizationScreen
import com.aibasedtyping.settings.PrivacyScreen
import com.aibasedtyping.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class Screen {
    SETTINGS,
    PRIVACY,
    DICTIONARY,
    PERSONALIZATION
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var userDictRepository: UserDictRepository

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var currentScreen by remember { mutableStateOf(Screen.SETTINGS) }

                when (currentScreen) {
                    Screen.SETTINGS -> {
                        SettingsScreen(
                            settingsRepository = settingsRepository,
                            onNavigateToPrivacy = { currentScreen = Screen.PRIVACY },
                            onNavigateToDictionary = { currentScreen = Screen.DICTIONARY },
                            onNavigateToPersonalization = { currentScreen = Screen.PERSONALIZATION }
                        )
                    }
                    Screen.PRIVACY -> {
                        PrivacyScreen(
                            userDictRepository = userDictRepository,
                            onNavigateBack = { currentScreen = Screen.SETTINGS }
                        )
                    }
                    Screen.DICTIONARY -> {
                        DictManagerScreen(
                            userDictRepository = userDictRepository,
                            onNavigateBack = { currentScreen = Screen.SETTINGS }
                        )
                    }
                    Screen.PERSONALIZATION -> {
                        PersonalizationScreen(
                            userProfileRepository = userProfileRepository,
                            onRetakeOnboarding = {
                                startActivity(Intent(this, OnboardingActivity::class.java))
                            },
                            onNavigateBack = { currentScreen = Screen.SETTINGS }
                        )
                    }
                }
            }
        }
    }
}

