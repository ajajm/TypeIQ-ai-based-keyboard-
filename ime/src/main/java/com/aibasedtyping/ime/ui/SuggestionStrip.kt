package com.aibasedtyping.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aibasedtyping.ime.ui.theme.LocalKeyboardColors

@Composable
fun SuggestionStrip(
    suggestions: List<String>,
    sentenceCompletion: String?,
    onSuggestionSelected: (Int, String) -> Unit,
    onSuggestionLongPress: (Int, String) -> Unit,
    onSentenceSelected: (String) -> Unit,
    isIncognito: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalKeyboardColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.keyboardBackground)
    ) {
        // Incognito or top bar indicator if needed
        if (isIncognito) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "🔒 Incognito",
                    color = colors.incognitoIndicator,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Suggestions row
        if (suggestions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                suggestions.take(3).forEachIndexed { index, suggestion ->
                    if (index > 0) {
                        Spacer(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(colors.suggestionDivider)
                        )
                    }

                    val isCenter = index == 1 || (suggestions.size == 1 && index == 0)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .pointerInput(suggestion) {
                                detectTapGestures(
                                    onTap = { onSuggestionSelected(index, suggestion) },
                                    onLongPress = { onSuggestionLongPress(index, suggestion) }
                                )
                            }
                            .semantics {
                                contentDescription = "Suggestion: $suggestion. Double tap to insert."
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = suggestion,
                            color = colors.suggestionText,
                            fontSize = 15.sp,
                            fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Sentence completion preview
        if (!sentenceCompletion.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.suggestionSelectedBackground.copy(alpha = 0.5f))
                    .clickable { onSentenceSelected(sentenceCompletion) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .semantics {
                        contentDescription = "Predicted sentence: $sentenceCompletion. Double tap to insert."
                    }
            ) {
                Text(
                    text = "✨ $sentenceCompletion",
                    color = colors.suggestionText,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

