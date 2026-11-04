package com.aibasedtyping.app.onboarding.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aibasedtyping.data.model.Language

@Composable
fun LanguageScreen(
    selectedLanguages: List<Language>,
    onLanguagesChanged: (List<Language>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("What language(s) do you type in?", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Select all that apply. The engine loads dictionary weights and n-gram profiles accordingly.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Language.entries.forEach { lang ->
            val isChecked = selectedLanguages.contains(lang)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val updated = if (isChecked) {
                            if (selectedLanguages.size > 1) selectedLanguages - lang else selectedLanguages
                        } else {
                            selectedLanguages + lang
                        }
                        onLanguagesChanged(updated)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(lang.displayName, style = MaterialTheme.typography.titleMedium)
                        Text(lang.code.uppercase(), style = MaterialTheme.typography.bodySmall)
                    }
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            val updated = if (checked) {
                                selectedLanguages + lang
                            } else {
                                if (selectedLanguages.size > 1) selectedLanguages - lang else selectedLanguages
                            }
                            onLanguagesChanged(updated)
                        }
                    )
                }
            }
        }
    }
}

