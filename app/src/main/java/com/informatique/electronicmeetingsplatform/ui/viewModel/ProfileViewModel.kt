package com.informatique.electronicmeetingsplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.login.LoginUseCase
import com.informatique.electronicmeetingsplatform.business.login.VerifySessionUseCase
import com.informatique.electronicmeetingsplatform.business.profile.PersonImageUseCase
import com.informatique.electronicmeetingsplatform.business.profile.ProfileUseCase
import com.informatique.electronicmeetingsplatform.data.model.profile.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Login screen
 * Manages authentication state and user interactions
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val personImageUseCase: PersonImageUseCase,
    private val verifySessionUseCase: VerifySessionUseCase
) : ViewModel() {

    // Profile state
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    // Profile state
    private val _personImageState = MutableStateFlow<PersonImageState>(PersonImageState.Idle)
    val personImageState: StateFlow<PersonImageState> = _personImageState.asStateFlow()

    // Session verification state
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Unknown)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init { profile() }

    fun profile() {

        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            when (val result = profileUseCase()) {
                is BusinessState.Success -> {
                    _profileState.value = ProfileState.Success(result.data.data)
                    personImage(result.data.data.person.personalPhotoPath)
                }
                is BusinessState.Error -> {
                    _profileState.value = ProfileState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _profileState.value = ProfileState.Loading
                }
            }
        }
    }

    fun personImage(fileName: String, expiryHours: Int = 24) {
        viewModelScope.launch {
            _personImageState.value = PersonImageState.Loading

            when (val result = personImageUseCase(PersonImageUseCase.Params(fileName, expiryHours))) {
                is BusinessState.Success -> {
                    _personImageState.value = PersonImageState.Success(result.data.url)
                    _sessionState.value = SessionState.Valid
                }
                is BusinessState.Error -> {
                    _personImageState.value = PersonImageState.Error(result.message)
                    _sessionState.value = SessionState.Invalid
                }
                is BusinessState.Loading -> {
                    _personImageState.value = PersonImageState.Loading
                }
            }
        }
    }

    /**
     * Verify if current session is valid
     */
    fun verifySession() {
        viewModelScope.launch {
            _sessionState.value = SessionState.Checking

            when (val result = verifySessionUseCase(Unit)) {
                is BusinessState.Success -> {
                    _sessionState.value = if (result.data) {
                        SessionState.Valid
                    } else {
                        SessionState.Invalid
                    }
                }
                is BusinessState.Error -> {
                    _sessionState.value = SessionState.Invalid
                }
                is BusinessState.Loading -> {
                    _sessionState.value = SessionState.Checking
                }
            }
        }
    }

}

/**
 * Profile state sealed class
 */
sealed class ProfileState {
    object Idle: ProfileState()
    object Loading: ProfileState()
    data class Success(val data: Data): ProfileState()
    data class Error(val message: String): ProfileState()
}

sealed class PersonImageState {
    object Idle: PersonImageState()
    object Loading: PersonImageState()
    data class Success(val url: String): PersonImageState()
    data class Error(val message: String): PersonImageState()
}
