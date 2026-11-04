package com.aibasedtyping.ime

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.aibasedtyping.ime.ui.Key
import com.aibasedtyping.ime.ui.KeyType
import com.aibasedtyping.ime.ui.KeyboardMode
import com.aibasedtyping.ime.ui.ShiftState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KeyController(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private var inputConnection: InputConnection? = null
    private var currentEditorInfo: EditorInfo? = null

    private val _shiftState = MutableStateFlow(ShiftState.NONE)
    val shiftState: StateFlow<ShiftState> = _shiftState.asStateFlow()

    private val _keyboardMode = MutableStateFlow(KeyboardMode.ALPHA)
    val keyboardMode: StateFlow<KeyboardMode> = _keyboardMode.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _sentenceCompletion = MutableStateFlow<String?>(null)
    val sentenceCompletion: StateFlow<String?> = _sentenceCompletion.asStateFlow()

    private val _actionLabel = MutableStateFlow("↵")
    val actionLabel: StateFlow<String> = _actionLabel.asStateFlow()

    private val _isIncognito = MutableStateFlow(false)
    val isIncognito: StateFlow<Boolean> = _isIncognito.asStateFlow()

    // Listener callbacks for CandidateEngine integration
    var onTypingChanged: ((textBeforeCursor: String) -> Unit)? = null
    var onWordCommitted: ((word: String) -> Unit)? = null

    fun setInputConnection(ic: InputConnection?) {
        this.inputConnection = ic
        updateSuggestions()
    }

    fun setEditorInfo(info: EditorInfo?) {
        this.currentEditorInfo = info
        updateActionLabel(info)
        checkIncognito(info)
    }

    fun setIncognito(incognito: Boolean) {
        _isIncognito.value = incognito
    }

    fun updateSuggestionsList(newSuggestions: List<String>, sentence: String? = null) {
        _suggestions.value = newSuggestions
        _sentenceCompletion.value = sentence
    }

    private fun checkIncognito(info: EditorInfo?) {
        if (info == null) return
        val variation = info.inputType and InputType.TYPE_MASK_VARIATION
        val isPassword = variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                (info.inputType and InputType.TYPE_CLASS_NUMBER != 0 &&
                        variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD)

        if (isPassword) {
            _isIncognito.value = true
        }
    }

    private fun updateActionLabel(info: EditorInfo?) {
        if (info == null) {
            _actionLabel.value = "↵"
            return
        }
        val action = info.imeOptions and (EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        _actionLabel.value = when (action) {
            EditorInfo.IME_ACTION_GO -> "Go"
            EditorInfo.IME_ACTION_SEARCH -> "Search"
            EditorInfo.IME_ACTION_SEND -> "Send"
            EditorInfo.IME_ACTION_NEXT -> "Next"
            EditorInfo.IME_ACTION_DONE -> "Done"
            else -> "↵"
        }
    }

    fun onKeyPress(key: Key) {
        when (key.type) {
            KeyType.CHARACTER -> {
                val charToCommit = if (_shiftState.value != ShiftState.NONE) {
                    key.label.uppercase()
                } else {
                    key.label
                }
                commitText(charToCommit)

                if (_shiftState.value == ShiftState.SHIFTED) {
                    _shiftState.value = ShiftState.NONE
                }
            }
            KeyType.SPACE -> {
                commitText(" ")
                autoShiftIfNeeded()
            }
            KeyType.BACKSPACE -> {
                deleteBack()
            }
            KeyType.ENTER -> {
                handleEnter()
            }
            KeyType.SHIFT -> {
                handleShift()
            }
            KeyType.SYMBOL_TOGGLE -> {
                _keyboardMode.value = KeyboardMode.SYMBOLS_1
            }
            KeyType.ALPHA_TOGGLE -> {
                _keyboardMode.value = KeyboardMode.ALPHA
            }
            KeyType.SYMBOL_PAGE -> {
                _keyboardMode.value = if (_keyboardMode.value == KeyboardMode.SYMBOLS_1) {
                    KeyboardMode.SYMBOLS_2
                } else {
                    KeyboardMode.SYMBOLS_1
                }
            }
            KeyType.COMMA -> commitText(",")
            KeyType.PERIOD -> {
                commitText(".")
                autoShiftIfNeeded()
            }
        }
        updateSuggestions()
    }

    fun onKeyRepeat(key: Key) {
        if (key.type == KeyType.BACKSPACE) {
            deleteBack()
            updateSuggestions()
        }
    }

    fun onKeyLongPress(key: Key) {
        if (key.type == KeyType.SHIFT) {
            _shiftState.value = ShiftState.CAPS_LOCK
        }
    }

    fun onSuggestionSelected(index: Int, word: String) {
        val ic = inputConnection ?: return
        val textBefore = ic.getTextBeforeCursor(50, 0)?.toString() ?: ""
        val lastWordMatch = Regex("(\\w+)$").find(textBefore)

        if (lastWordMatch != null) {
            val replaceLen = lastWordMatch.value.length
            ic.deleteSurroundingText(replaceLen, 0)
        }

        ic.commitText("$word ", 1)
        onWordCommitted?.invoke(word)
        autoShiftIfNeeded()
        updateSuggestions()
    }

    fun onSentenceSelected(sentence: String) {
        val ic = inputConnection ?: return
        val textBefore = ic.getTextBeforeCursor(50, 0)?.toString() ?: ""
        val lastWordMatch = Regex("(\\w+)$").find(textBefore)

        if (lastWordMatch != null) {
            val replaceLen = lastWordMatch.value.length
            ic.deleteSurroundingText(replaceLen, 0)
        }

        ic.commitText("$sentence ", 1)
        autoShiftIfNeeded()
        updateSuggestions()
    }

    fun onSuggestionLongPress(index: Int, word: String) {
        // Remove or block candidate
    }

    private fun commitText(text: String) {
        inputConnection?.commitText(text, 1)
        if (text == " ") {
            val textBefore = inputConnection?.getTextBeforeCursor(50, 0)?.toString() ?: ""
            val words = textBefore.trim().split(Regex("\\s+"))
            if (words.isNotEmpty()) {
                onWordCommitted?.invoke(words.last())
            }
        }
    }

    private fun deleteBack() {
        val selected = inputConnection?.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            inputConnection?.commitText("", 1)
        } else {
            inputConnection?.deleteSurroundingText(1, 0)
        }
    }

    private fun handleShift() {
        _shiftState.value = when (_shiftState.value) {
            ShiftState.NONE -> ShiftState.SHIFTED
            ShiftState.SHIFTED -> ShiftState.CAPS_LOCK
            ShiftState.CAPS_LOCK -> ShiftState.NONE
        }
    }

    private fun handleEnter() {
        val info = currentEditorInfo
        val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            inputConnection?.performEditorAction(action)
        } else {
            commitText("\n")
        }
    }

    private fun autoShiftIfNeeded() {
        val before = inputConnection?.getTextBeforeCursor(3, 0)?.toString() ?: ""
        if (before.endsWith(". ") || before.endsWith("? ") || before.endsWith("! ") || before.isEmpty()) {
            if (_shiftState.value == ShiftState.NONE) {
                _shiftState.value = ShiftState.SHIFTED
            }
        }
    }

    private fun updateSuggestions() {
        val before = inputConnection?.getTextBeforeCursor(100, 0)?.toString() ?: ""
        onTypingChanged?.invoke(before)
    }

    fun reset() {
        inputConnection = null
        currentEditorInfo = null
        _shiftState.value = ShiftState.NONE
        _keyboardMode.value = KeyboardMode.ALPHA
        _suggestions.value = emptyList()
        _sentenceCompletion.value = null
    }
}

