package com.informatique.electronicmeetingsplatform.data.repository.auth

import android.content.Context
import com.informatique.electronicmeetingsplatform.data.datastorehelper.TokenManager
import com.informatique.electronicmeetingsplatform.data.model.auth.LoginRequest
import com.informatique.electronicmeetingsplatform.data.model.auth.LoginResponse
import com.informatique.electronicmeetingsplatform.data.model.auth.LogoutRequest
import com.informatique.electronicmeetingsplatform.data.model.auth.RefreshTokenRequest
import com.informatique.electronicmeetingsplatform.data.model.profile.Data
import com.informatique.electronicmeetingsplatform.data.model.profile.ProfileResponse
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageRequest
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageResponse
import com.informatique.electronicmeetingsplatform.data.remote.auth.AuthApiService
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.session.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository
 * Manages authentication operations and local token storage
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
    @param:ApplicationContext private val context: Context
) : AuthRepository {

    override suspend fun login(email: String, password: String): Flow<ApiResponse<LoginResponse>> = flow {
        emit(ApiResponse.Loading)

        // Get device information
//        val deviceId = getOrCreateDeviceId()
//        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        val request = LoginRequest(
            email = email,
            password = password
        )

        val response = authApiService.login(request)

        // Check for session expiration
        checkSessionExpiration(response)

        // If login successful, save tokens
        when (response) {
            is ApiResponse.Success -> {
                saveAuthToken(
                    token = response.data.accessToken,
                    refreshToken = response.data.refreshToken
                )
            }
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun refreshToken(refreshToken: String): Flow<ApiResponse<LoginResponse>> = flow {
        emit(ApiResponse.Loading)

        val request = RefreshTokenRequest(refreshToken = refreshToken)
        val response = authApiService.refreshToken(request)

        // If refresh successful, save new tokens
        when (response) {
            is ApiResponse.Success -> {
                saveAuthToken(
                    token = response.data.accessToken,
                    refreshToken = response.data.refreshToken
                )
            }
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun logout(allDevices: Boolean): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)

        val refreshToken = getRefreshToken()
        val request = LogoutRequest(
            refreshToken = refreshToken,
            allDevices = allDevices
        )

        val response = authApiService.logout(request)

        // Always clear local tokens on logout, even if API call fails
        clearAuthData()

        emit(response)
    }

    override suspend fun verifySession(): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)

        // First check if token exists locally
        if (!isLoggedIn()) {
            emit(ApiResponse.Success(false))
            return@flow
        }

        // Then verify with server
        val response = authApiService.verifySession()

        // Check for session expiration
        checkSessionExpiration(response)

        // If session invalid, clear local tokens
        if (response is ApiResponse.Success && !response.data) {
            clearAuthData()
        }

        emit(response)
    }

    override suspend fun requestPasswordReset(email: String): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        emit(authApiService.requestPasswordReset(email))
    }

    override suspend fun verifyResetCode(email: String, code: String): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)
        emit(authApiService.verifyResetCode(email, code))
    }

    override suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        emit(authApiService.resetPassword(email, code, newPassword))
    }

    override suspend fun profile(): Flow<ApiResponse<ProfileResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = authApiService.profile()

        // Check for session expiration
        checkSessionExpiration(response)

        // If login successful, save tokens
        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun personImage(
        fileName: String,
        expiryHours: Int
    ): Flow<ApiResponse<PersonImageResponse>> = flow {
        emit(ApiResponse.Loading)

        val request = PersonImageRequest(
            fileName = fileName,
            expiryHours = expiryHours
        )

        val response = authApiService.personImage(request)

        // Check for session expiration
        checkSessionExpiration(response)

        // If login successful, save tokens
        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun saveAuthToken(token: String, refreshToken: String?) {
        TokenManager.saveOAuthTokens(
            context = context,
            accessToken = token,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = 3600 // 1 hour default, adjust based on your API
        )
    }

    override suspend fun getAccessToken(): String? {
        return TokenManager.getAccessToken(context)
    }

    override suspend fun getRefreshToken(): String? {
        return TokenManager.getRefreshToken(context)
    }

    override suspend fun clearAuthData() {
        TokenManager.clearToken(context)
    }

    override suspend fun isLoggedIn(): Boolean {
        val token = getAccessToken()
        return !token.isNullOrEmpty()
    }

    /**
     * Check if response indicates session expiration and trigger alert
     */
    private fun checkSessionExpiration(response: ApiResponse<*>) {
        if (response is ApiResponse.Error && response.code == 401) {
            sessionManager.triggerSessionExpired()
        }
    }

    /**
     * Get or create a unique device ID for this installation
     */
    private fun getOrCreateDeviceId(): String {
        val sharedPrefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        var deviceId = sharedPrefs.getString("device_id", null)

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            sharedPrefs.edit().putString("device_id", deviceId).apply()
        }

        return deviceId
    }
}

