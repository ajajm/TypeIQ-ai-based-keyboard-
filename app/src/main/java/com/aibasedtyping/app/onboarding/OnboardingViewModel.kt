package com.aibasedtyping.app.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aibasedtyping.data.model.Language
import com.aibasedtyping.data.model.ModelSize
import com.aibasedtyping.data.model.Profession
import com.aibasedtyping.data.model.TypingStyle
import com.aibasedtyping.data.model.Usage
import com.aibasedtyping.data.model.UserProfile
import com.aibasedtyping.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun updateLanguages(languages: List<Language>) {
        _userProfile.value = _userProfile.value.copy(languages = languages)
    }

    fun updateTypingStyle(style: TypingStyle) {
        _userProfile.value = _userProfile.value.copy(typingStyle = style)
    }

    fun updateUsage(usage: Usage) {
        _userProfile.value = _userProfile.value.copy(usage = usage)
    }

    fun updateProfession(profession: Profession) {
        _userProfile.value = _userProfile.value.copy(profession = profession)
    }

    fun updateSentencePrediction(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(sentencePredictionEnabled = enabled)
    }

    fun updateModelSize(modelSize: ModelSize) {
        _userProfile.value = _userProfile.value.copy(modelSize = modelSize)
    }

    fun nextPage() {
        if (_currentPage.value < 5) {
            _currentPage.value += 1
        }
    }

    fun prevPage() {
        if (_currentPage.value > 0) {
            _currentPage.value -= 1
        }
    }

    fun completeOnboarding(onFinished: () -> Unit) {
        viewModelScope.launch {
            val finishedProfile = _userProfile.value.copy(onboardingCompleted = true)
            userProfileRepository.saveProfile(finishedProfile)
            onFinished()
        }
    }
}

