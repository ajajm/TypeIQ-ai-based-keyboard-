package com.aibasedtyping.app.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aibasedtyping.app.MainActivity
import com.aibasedtyping.app.onboarding.screens.LanguageScreen
import com.aibasedtyping.app.onboarding.screens.ModelSizeScreen
import com.aibasedtyping.app.onboarding.screens.ProfessionScreen
import com.aibasedtyping.app.onboarding.screens.SentenceToggleScreen
import com.aibasedtyping.app.onboarding.screens.TypingStyleScreen
import com.aibasedtyping.app.onboarding.screens.UsageScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                OnboardingScreen(
                    viewModel = viewModel,
                    onFinish = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit
) {
    val page by viewModel.currentPage.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val totalPages = 6

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Page Indicator Dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(totalPages) { index ->
                        val isSelected = page == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (page > 0) {
                        OutlinedButton(onClick = { viewModel.prevPage() }) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.size(1.dp))
                    }

                    if (page < totalPages - 1) {
                        Button(onClick = { viewModel.nextPage() }) {
                            Text("Next")
                        }
                    } else {
                        Button(onClick = { viewModel.completeOnboarding(onFinish) }) {
                            Text("Get Started")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            when (page) {
                0 -> LanguageScreen(
                    selectedLanguages = profile.languages,
                    onLanguagesChanged = { viewModel.updateLanguages(it) }
                )
                1 -> TypingStyleScreen(
                    selectedStyle = profile.typingStyle,
                    onStyleSelected = { viewModel.updateTypingStyle(it) }
                )
                2 -> UsageScreen(
                    selectedUsage = profile.usage,
                    onUsageSelected = { viewModel.updateUsage(it) }
                )
                3 -> ProfessionScreen(
                    selectedProfession = profile.profession,
                    onProfessionSelected = { viewModel.updateProfession(it) }
                )
                4 -> SentenceToggleScreen(
                    enabled = profile.sentencePredictionEnabled,
                    onToggleChanged = { viewModel.updateSentencePrediction(it) }
                )
                5 -> ModelSizeScreen(
                    selectedModelSize = profile.modelSize,
                    onModelSizeSelected = { viewModel.updateModelSize(it) }
                )
            }
        }
    }
}

