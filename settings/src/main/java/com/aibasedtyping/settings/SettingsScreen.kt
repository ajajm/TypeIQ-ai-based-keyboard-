package com.aibasedtyping.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aibasedtyping.data.repository.SettingsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToPersonalization: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val incognito by settingsRepository.incognitoMode.collectAsState(initial = false)
    val sound by settingsRepository.soundEnabled.collectAsState(initial = true)
    val vibration by settingsRepository.vibrationEnabled.collectAsState(initial = true)
    val sentencePrediction by settingsRepository.sentencePrediction.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Based Typing Settings") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Typing & Predictions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "Sentence Prediction",
                        subtitle = "Generate full next-sentence suggestions based on your typing style",
                        checked = sentencePrediction,
                        onCheckedChange = { coroutineScope.launch { settingsRepository.setSentencePrediction(it) } }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingToggleRow(
                        title = "Incognito Mode",
                        subtitle = "Temporarily disable learning and suggestions",
                        checked = incognito,
                        onCheckedChange = { coroutineScope.launch { settingsRepository.setIncognito(it) } }
                    )
                }
            }

            Text("Preferences", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "Keypress Sound",
                        subtitle = "Play sound when tapping keys",
                        checked = sound,
                        onCheckedChange = { coroutineScope.launch { settingsRepository.setSoundEnabled(it) } }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingToggleRow(
                        title = "Haptic Feedback",
                        subtitle = "Vibrate slightly on keypress",
                        checked = vibration,
                        onCheckedChange = { coroutineScope.launch { settingsRepository.setVibrationEnabled(it) } }
                    )
                }
            }

            Text("Data & Customization", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingNavigationRow(
                        title = "Personalization Profile",
                        subtitle = "Review or retake onboarding questions",
                        onClick = onNavigateToPersonalization
                    )
                    HorizontalDivider()
                    SettingNavigationRow(
                        title = "Personal Dictionary",
                        subtitle = "View, export, or import learned words",
                        onClick = onNavigateToDictionary
                    )
                    HorizontalDivider()
                    SettingNavigationRow(
                        title = "Privacy & Security",
                        subtitle = "Learn how your data stays 100% on-device",
                        onClick = onNavigateToPrivacy
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingNavigationRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

