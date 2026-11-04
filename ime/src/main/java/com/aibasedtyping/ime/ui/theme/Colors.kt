package com.aibasedtyping.ime.ui.theme

import androidx.compose.ui.graphics.Color

data class KeyboardColors(
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyBackgroundPressed: Color,
    val specialKeyBackground: Color,
    val spaceBarBackground: Color,
    val keyText: Color,
    val specialKeyText: Color,
    val suggestionText: Color,
    val suggestionSelectedBackground: Color,
    val suggestionDivider: Color,
    val incognitoIndicator: Color,
    val popupBackground: Color,
    val popupText: Color
)

val LightKeyboardColors = KeyboardColors(
    keyboardBackground = Color(0xFFD3D6DB),
    keyBackground = Color(0xFFFFFFFF),
    keyBackgroundPressed = Color(0xFFBEC1C6),
    specialKeyBackground = Color(0xFFAEB3BC),
    spaceBarBackground = Color(0xFFFFFFFF),
    keyText = Color(0xFF1B1B1F),
    specialKeyText = Color(0xFF1B1B1F),
    suggestionText = Color(0xFF1B1B1F),
    suggestionSelectedBackground = Color(0xFFE0E0E0),
    suggestionDivider = Color(0xFFCCCCCC),
    incognitoIndicator = Color(0xFF6750A4),
    popupBackground = Color(0xFFFFFFFF),
    popupText = Color(0xFF1B1B1F)
)

val DarkKeyboardColors = KeyboardColors(
    keyboardBackground = Color(0xFF2C2C2E),
    keyBackground = Color(0xFF4A4A4C),
    keyBackgroundPressed = Color(0xFF636366),
    specialKeyBackground = Color(0xFF3A3A3C),
    spaceBarBackground = Color(0xFF4A4A4C),
    keyText = Color(0xFFFFFFFF),
    specialKeyText = Color(0xFFD1D1D6),
    suggestionText = Color(0xFFFFFFFF),
    suggestionSelectedBackground = Color(0xFF48484A),
    suggestionDivider = Color(0xFF48484A),
    incognitoIndicator = Color(0xFFD0BCFF),
    popupBackground = Color(0xFF3A3A3C),
    popupText = Color(0xFFFFFFFF)
)
