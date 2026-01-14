package com.informatique.electronicmeetingsplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.login.LoginUseCase
import com.informatique.electronicmeetingsplatform.business.login.LogoutUseCase
import com.informatique.electronicmeetingsplatform.business.login.VerifySessionUseCase
import com.informatique.electronicmeetingsplatform.data.model.auth.LoginResponse
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val verifySessionUseCase: VerifySessionUseCase
) : ViewModel() {

    // Login state
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Session verification state
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Unknown)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    // Form validation states
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    /**
     * Login with username and password
     */
    fun login(email: String, password: String) {
        // Clear previous errors
        _emailError.value = null
        _passwordError.value = null

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when (val result = loginUseCase(LoginUseCase.Params(email, password))) {
                is BusinessState.Success -> {
                    _loginState.value = LoginState.Success(result.data)
                    _sessionState.value = SessionState.Valid
                }
                is BusinessState.Error -> {
                    _loginState.value = LoginState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _loginState.value = LoginState.Loading
                }
            }
        }
    }

    /**
     * Logout current user
     */
    fun logout(fromAllDevices: Boolean = false) {
        viewModelScope.launch {
            when (logoutUseCase(LogoutUseCase.Params(fromAllDevices))) {
                is BusinessState.Success -> {
                    _sessionState.value = SessionState.Invalid
                    _loginState.value = LoginState.Idle
                }
                is BusinessState.Error -> {
                    // Still consider logout successful since local data is cleared
                    _sessionState.value = SessionState.Invalid
                    _loginState.value = LoginState.Idle
                }
                is BusinessState.Loading -> {
                    // Loading state
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

    /**
     * Reset login state to idle
     */
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
        _emailError.value = null
        _passwordError.value = null
    }

    /**
     * Validate email input field
     */
    fun validateEmailInput(email: String): Boolean {
        var isValid = true

        if (email.isBlank()) {
            _emailError.value = "Email cannot be empty"
            isValid = false
        } else if (email.length < 3) {
            _emailError.value = "Email must be at least 3 characters"
            isValid = false
        }

        return isValid
    }

    /**
     * Validate password input field
     */
    fun validatePasswordInput(password: String): Boolean {
        var isValid = true

        if (password.isBlank()) {
            _passwordError.value = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    /**
     * Clear specific field error
     */
    fun clearEmailError() {
        _emailError.value = null
    }

    fun clearPasswordError() {
        _passwordError.value = null
    }
}

/**
 * Login state sealed class
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val data: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * Session state sealed class
 */
sealed class SessionState {
    object Unknown : SessionState()
    object Checking : SessionState()
    object Valid : SessionState()
    object Invalid : SessionState()
}
