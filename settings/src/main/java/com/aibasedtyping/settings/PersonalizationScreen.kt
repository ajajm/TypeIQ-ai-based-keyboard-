package com.aibasedtyping.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aibasedtyping.data.model.UserProfile
import com.aibasedtyping.data.repository.UserProfileRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizationScreen(
    userProfileRepository: UserProfileRepository,
    onRetakeOnboarding: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by userProfileRepository.userProfile.collectAsState(initial = UserProfile())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personalization Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current Profile Settings", style = MaterialTheme.typography.titleMedium)
                    Text("• Languages: ${profile.languages.joinToString { it.displayName }}")
                    Text("• Typing Style: ${profile.typingStyle.displayName}")
                    Text("• Primary Usage: ${profile.usage.displayName}")
                    Text("• Profession Domain: ${profile.profession.displayName}")
                    Text("• Sentence Predictions: ${if (profile.sentencePredictionEnabled) "Enabled" else "Disabled"}")
                    Text("• Model Size: ${profile.modelSize.displayName}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetakeOnboarding,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retake Onboarding Questions")
            }
        }
    }
}

