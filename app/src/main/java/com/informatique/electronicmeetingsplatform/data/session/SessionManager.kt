package com.informatique.electronicmeetingsplatform.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Session manager to handle session expiration across the entire app
 */
@Singleton
class SessionManager @Inject constructor() {

    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired: StateFlow<Boolean> = _sessionExpired.asStateFlow()

    /**
     * Trigger session expiration
     * This will show the alert dialog across the app
     */
    fun triggerSessionExpired() {
        _sessionExpired.value = true
    }

    /**
     * Reset session expiration state
     * Call this after user acknowledges the alert
     */
    fun resetSessionExpired() {
        _sessionExpired.value = false
    }

    /**
     * Check if session is expired
     */
    fun isSessionExpired(): Boolean {
        return _sessionExpired.value
    }
}



