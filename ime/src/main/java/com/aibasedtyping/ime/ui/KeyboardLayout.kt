package com.aibasedtyping.ime.ui

data class Key(
    val label: String,
    val code: Int,
    val width: Float = 1f,
    val type: KeyType = KeyType.CHARACTER,
    val icon: String? = null,
    val longPressChars: List<String> = emptyList(),
    val contentDescription: String = label
)

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    SYMBOL_TOGGLE,
    ALPHA_TOGGLE,
    SYMBOL_PAGE,
    COMMA,
    PERIOD
}

enum class ShiftState {
    NONE,
    SHIFTED,
    CAPS_LOCK
}

enum class KeyboardMode {
    ALPHA,
    SYMBOLS_1,
    SYMBOLS_2
}

object KeyboardLayouts {

    val alphaRows: List<List<Key>> = listOf(
        listOf(
            Key("q", 'q'.code, longPressChars = listOf("1")),
            Key("w", 'w'.code, longPressChars = listOf("2")),
            Key("e", 'e'.code, longPressChars = listOf("3", "è", "é", "ê", "ë")),
            Key("r", 'r'.code, longPressChars = listOf("4")),
            Key("t", 't'.code, longPressChars = listOf("5")),
            Key("y", 'y'.code, longPressChars = listOf("6")),
            Key("u", 'u'.code, longPressChars = listOf("7", "ù", "ú", "û", "ü")),
            Key("i", 'i'.code, longPressChars = listOf("8", "ì", "í", "î", "ï")),
            Key("o", 'o'.code, longPressChars = listOf("9", "ò", "ó", "ô", "ö")),
            Key("p", 'p'.code, longPressChars = listOf("0"))
        ),
        listOf(
            Key("a", 'a'.code, longPressChars = listOf("à", "á", "â", "ä", "æ")),
            Key("s", 's'.code, longPressChars = listOf("ß")),
            Key("d", 'd'.code),
            Key("f", 'f'.code),
            Key("g", 'g'.code),
            Key("h", 'h'.code),
            Key("j", 'j'.code),
            Key("k", 'k'.code),
            Key("l", 'l'.code)
        ),
        listOf(
            Key("⇧", 0, width = 1.5f, type = KeyType.SHIFT, icon = "shift", contentDescription = "Shift"),
            Key("z", 'z'.code),
            Key("x", 'x'.code),
            Key("c", 'c'.code, longPressChars = listOf("ç")),
            Key("v", 'v'.code),
            Key("b", 'b'.code),
            Key("n", 'n'.code, longPressChars = listOf("ñ")),
            Key("m", 'm'.code),
            Key("⌫", 0, width = 1.5f, type = KeyType.BACKSPACE, icon = "backspace", contentDescription = "Backspace")
        ),
        listOf(
            Key("?123", 0, width = 1.5f, type = KeyType.SYMBOL_TOGGLE, contentDescription = "Symbol keyboard"),
            Key(",", ','.code, type = KeyType.COMMA),
            Key(" ", ' '.code, width = 4f, type = KeyType.SPACE, contentDescription = "Space"),
            Key(".", '.'.code, type = KeyType.PERIOD, longPressChars = listOf("!", "?", ",", ":", ";")),
            Key("↵", 0, width = 1.5f, type = KeyType.ENTER, icon = "enter", contentDescription = "Enter")
        )
    )

    val symbolRows1: List<List<Key>> = listOf(
        listOf(
            Key("1", '1'.code), Key("2", '2'.code), Key("3", '3'.code),
            Key("4", '4'.code), Key("5", '5'.code), Key("6", '6'.code),
            Key("7", '7'.code), Key("8", '8'.code), Key("9", '9'.code),
            Key("0", '0'.code)
        ),
        listOf(
            Key("@", '@'.code), Key("#", '#'.code), Key("$", '$'.code),
            Key("%", '%'.code), Key("&", '&'.code), Key("-", '-'.code),
            Key("+", '+'.code), Key("(", '('.code), Key(")", ')'.code)
        ),
        listOf(
            Key("=\\<", 0, width = 1.5f, type = KeyType.SYMBOL_PAGE, contentDescription = "More symbols"),
            Key("*", '*'.code), Key("\"", '"'.code), Key("'", '\''.code),
            Key(":", ':'.code), Key(";", ';'.code), Key("!", '!'.code),
            Key("?", '?'.code),
            Key("⌫", 0, width = 1.5f, type = KeyType.BACKSPACE, icon = "backspace", contentDescription = "Backspace")
        ),
        listOf(
            Key("ABC", 0, width = 1.5f, type = KeyType.ALPHA_TOGGLE, contentDescription = "Letter keyboard"),
            Key(",", ','.code, type = KeyType.COMMA),
            Key(" ", ' '.code, width = 4f, type = KeyType.SPACE, contentDescription = "Space"),
            Key(".", '.'.code, type = KeyType.PERIOD),
            Key("↵", 0, width = 1.5f, type = KeyType.ENTER, icon = "enter", contentDescription = "Enter")
        )
    )

    val symbolRows2: List<List<Key>> = listOf(
        listOf(
            Key("~", '~'.code), Key("`", '`'.code), Key("|", '|'.code),
            Key("•", '•'.code), Key("√", '√'.code), Key("π", 'π'.code),
            Key("÷", '÷'.code), Key("×", '×'.code), Key("¶", '¶'.code),
            Key("∆", '∆'.code)
        ),
        listOf(
            Key("£", '£'.code), Key("¥", '¥'.code), Key("€", '€'.code),
            Key("¢", '¢'.code), Key("^", '^'.code), Key("°", '°'.code),
            Key("=", '='.code), Key("{", '{'.code), Key("}", '}'.code)
        ),
        listOf(
            Key("?123", 0, width = 1.5f, type = KeyType.SYMBOL_PAGE, contentDescription = "Number symbols"),
            Key("\\", '\\'.code), Key("©", '©'.code), Key("®", '®'.code),
            Key("™", '™'.code), Key("℅", '℅'.code), Key("[", '['.code),
            Key("]", ']'.code),
            Key("⌫", 0, width = 1.5f, type = KeyType.BACKSPACE, icon = "backspace", contentDescription = "Backspace")
        ),
        listOf(
            Key("ABC", 0, width = 1.5f, type = KeyType.ALPHA_TOGGLE, contentDescription = "Letter keyboard"),
            Key(",", ','.code, type = KeyType.COMMA),
            Key(" ", ' '.code, width = 4f, type = KeyType.SPACE, contentDescription = "Space"),
            Key(".", '.'.code, type = KeyType.PERIOD),
            Key("↵", 0, width = 1.5f, type = KeyType.ENTER, icon = "enter", contentDescription = "Enter")
        )
    )
}
