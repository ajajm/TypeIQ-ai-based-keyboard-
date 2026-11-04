package com.aibasedtyping.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aibasedtyping.data.db.TypingBehaviourDao
import com.aibasedtyping.data.model.UserProfile
import com.aibasedtyping.data.repository.SettingsRepository
import com.aibasedtyping.data.repository.UserDictRepository
import com.aibasedtyping.data.repository.UserProfileRepository
import com.aibasedtyping.ime.ui.KeyboardView
import com.aibasedtyping.ime.ui.SuggestionStrip
import com.aibasedtyping.ime.ui.theme.KeyboardTheme
import com.aibasedtyping.prediction.CandidateEngine
import com.aibasedtyping.prediction.behaviour.TypingBehaviourTracker
import com.aibasedtyping.prediction.lm.MiniLMEngine
import com.aibasedtyping.prediction.lm.SentencePredictor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AiTypingIMEService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = store

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    @Inject
    lateinit var userDictRepository: UserDictRepository

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var typingBehaviourDao: TypingBehaviourDao

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var predictionJob: Job? = null

    private lateinit var keyController: KeyController
    private lateinit var candidateEngine: CandidateEngine
    private lateinit var behaviourTracker: TypingBehaviourTracker
    private lateinit var sentencePredictor: SentencePredictor
    private var currentUserProfile: UserProfile = UserProfile()

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        keyController = KeyController(serviceScope)

        // Initialize on-device engines
        behaviourTracker = TypingBehaviourTracker(typingBehaviourDao, userDictRepository)
        candidateEngine = CandidateEngine(userDictRepository, behaviourTracker)
        sentencePredictor = SentencePredictor(MiniLMEngine(), behaviourTracker)

        // Listen for user profile updates
        serviceScope.launch {
            userProfileRepository.userProfile.collect { profile ->
                currentUserProfile = profile
            }
        }

        // Wire candidate & sentence prediction loop
        keyController.onTypingChanged = { textBeforeCursor ->
            behaviourTracker.onKeystroke()
            predictionJob?.cancel()
            predictionJob = serviceScope.launch {
                val candidates = candidateEngine.getCandidates(textBeforeCursor, limit = 3)
                val sentence = sentencePredictor.predictSentence(textBeforeCursor, currentUserProfile)
                keyController.updateSuggestionsList(candidates, sentence)
            }
        }

        keyController.onWordCommitted = { word ->
            val isIncognito = keyController.isIncognito.value
            behaviourTracker.onWordCommitted(word, isIncognito)
        }
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        return ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setViewTreeLifecycleOwner(this@AiTypingIMEService)
            setViewTreeSavedStateRegistryOwner(this@AiTypingIMEService)
            setViewTreeViewModelStoreOwner(this@AiTypingIMEService)

            setContent {
                val shiftState by keyController.shiftState.collectAsState()
                val keyboardMode by keyController.keyboardMode.collectAsState()
                val suggestions by keyController.suggestions.collectAsState()
                val sentenceCompletion by keyController.sentenceCompletion.collectAsState()
                val actionLabel by keyController.actionLabel.collectAsState()
                val isIncognito by keyController.isIncognito.collectAsState()

                KeyboardTheme {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SuggestionStrip(
                            suggestions = suggestions,
                            sentenceCompletion = sentenceCompletion,
                            onSuggestionSelected = { index, word ->
                                keyController.onSuggestionSelected(index, word)
                            },
                            onSuggestionLongPress = { index, word ->
                                keyController.onSuggestionLongPress(index, word)
                            },
                            onSentenceSelected = { sentence ->
                                keyController.onSentenceSelected(sentence)
                            },
                            isIncognito = isIncognito
                        )

                        KeyboardView(
                            mode = keyboardMode,
                            shiftState = shiftState,
                            actionLabel = actionLabel,
                            onKeyPress = { keyController.onKeyPress(it) },
                            onKeyLongPress = { keyController.onKeyLongPress(it) },
                            onKeyRepeat = { keyController.onKeyRepeat(it) }
                        )
                    }
                }
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        keyController.setEditorInfo(attribute)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        keyController.setInputConnection(currentInputConnection)
        keyController.setEditorInfo(info)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onFinishInputView(finishingInput)
    }

    override fun onFinishInput() {
        super.onFinishInput()
        keyController.reset()
        behaviourTracker.resetSession()
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        super.onDestroy()
    }
}
