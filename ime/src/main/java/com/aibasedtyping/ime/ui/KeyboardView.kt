package com.aibasedtyping.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.aibasedtyping.ime.ui.theme.LocalKeyboardColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun KeyboardView(
    mode: KeyboardMode,
    shiftState: ShiftState,
    actionLabel: String,
    onKeyPress: (Key) -> Unit,
    onKeyLongPress: (Key) -> Unit,
    onKeyRepeat: (Key) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalKeyboardColors.current
    val rows = when (mode) {
        KeyboardMode.ALPHA -> KeyboardLayouts.alphaRows
        KeyboardMode.SYMBOLS_1 -> KeyboardLayouts.symbolRows1
        KeyboardMode.SYMBOLS_2 -> KeyboardLayouts.symbolRows2
    }

    var popupKey by remember { mutableStateOf<Key?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.keyboardBackground)
            .padding(horizontal = 3.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowKeys.forEach { key ->
                    KeyItem(
                        key = key,
                        shiftState = shiftState,
                        actionLabel = actionLabel,
                        colors = colors,
                        modifier = Modifier.weight(key.width),
                        onPress = { onKeyPress(it) },
                        onLongPress = {
                            if (it.longPressChars.isNotEmpty()) {
                                popupKey = it
                            } else {
                                onKeyLongPress(it)
                            }
                        },
                        onRepeat = { onKeyRepeat(it) }
                    )
                }
            }
        }
    }

    // Popup for alternate characters
    popupKey?.let { key ->
        Popup(
            onDismissRequest = { popupKey = null },
            properties = PopupProperties(focusable = true)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                    .background(colors.popupBackground, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    key.longPressChars.forEach { altChar ->
                        Box(
                            modifier = Modifier
                                .height(44.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.keyBackground)
                                .clickable {
                                    val charCode = if (altChar.isNotEmpty()) altChar[0].code else 0
                                    onKeyPress(Key(label = altChar, code = charCode, type = KeyType.CHARACTER))
                                    popupKey = null
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = altChar,
                                color = colors.popupText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyItem(
    key: Key,
    shiftState: ShiftState,
    actionLabel: String,
    colors: com.aibasedtyping.ime.ui.theme.KeyboardColors,
    modifier: Modifier = Modifier,
    onPress: (Key) -> Unit,
    onLongPress: (Key) -> Unit,
    onRepeat: (Key) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    var repeatJob by remember { mutableStateOf<Job?>(null) }

    val displayText = when {
        key.type == KeyType.ENTER -> actionLabel.ifEmpty { "↵" }
        key.type == KeyType.SHIFT -> when (shiftState) {
            ShiftState.CAPS_LOCK -> "⇪"
            ShiftState.SHIFTED -> "⇧"
            ShiftState.NONE -> "⇧"
        }
        key.type == KeyType.CHARACTER && shiftState != ShiftState.NONE -> key.label.uppercase()
        else -> key.label
    }

    val isSpecial = key.type in listOf(
        KeyType.SHIFT,
        KeyType.BACKSPACE,
        KeyType.ENTER,
        KeyType.SYMBOL_TOGGLE,
        KeyType.ALPHA_TOGGLE,
        KeyType.SYMBOL_PAGE
    )

    val keyBg = when {
        isPressed -> colors.keyBackgroundPressed
        key.type == KeyType.SPACE -> colors.spaceBarBackground
        isSpecial -> colors.specialKeyBackground
        else -> colors.keyBackground
    }

    val textColor = if (isSpecial) colors.specialKeyText else colors.keyText

    Box(
        modifier = modifier
            .height(46.dp)
            .shadow(1.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(keyBg)
            .semantics { contentDescription = key.contentDescription }
            .pointerInput(key) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (key.type == KeyType.BACKSPACE) {
                            repeatJob = coroutineScope.launch {
                                delay(400)
                                while (isPressed) {
                                    onRepeat(key)
                                    delay(50)
                                }
                            }
                        }
                        tryAwaitRelease()
                        isPressed = false
                        repeatJob?.cancel()
                    },
                    onTap = { onPress(key) },
                    onLongPress = { onLongPress(key) }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            color = textColor,
            fontSize = if (key.type == KeyType.ENTER && actionLabel.length > 2) 13.sp else 18.sp,
            fontWeight = if (key.type == KeyType.ENTER || isSpecial) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

