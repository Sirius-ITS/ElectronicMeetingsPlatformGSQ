package com.informatique.electronicmeetingsplatform.data.repository.auth

import com.informatique.electronicmeetingsplatform.data.model.auth.LoginResponse
import com.informatique.electronicmeetingsplatform.data.model.profile.ProfileResponse
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import kotlinx.coroutines.flow.Flow

/**
 * Authentication repository interface
 * Abstraction layer between data sources and business logic
 */
interface AuthRepository {

    /**
     * Login with credentials
     * @param email User's username or email
     * @param password User's password
     * @return Flow emitting login response states
     */
    suspend fun login(email: String, password: String): Flow<ApiResponse<LoginResponse>>

    /**
     * Refresh authentication token
     * @param refreshToken Current refresh token
     * @return Flow emitting refresh response states
     */
    suspend fun refreshToken(refreshToken: String): Flow<ApiResponse<LoginResponse>>

    /**
     * Logout current user
     * @param allDevices If true, logout from all devices
     * @return Flow emitting logout response states
     */
    suspend fun logout(allDevices: Boolean = false): Flow<ApiResponse<Unit>>

    /**
     * Verify if current session is valid
     * @return Flow emitting session validity
     */
    suspend fun verifySession(): Flow<ApiResponse<Boolean>>

    /**
     * Request password reset email
     * @param email User's email address
     * @return Flow emitting request status
     */
    suspend fun requestPasswordReset(email: String): Flow<ApiResponse<Unit>>

    /**
     * Verify password reset code
     * @param email User's email
     * @param code Reset code
     * @return Flow emitting verification status
     */
    suspend fun verifyResetCode(email: String, code: String): Flow<ApiResponse<Boolean>>

    /**
     * Reset password with verified code
     * @param email User's email
     * @param code Reset code
     * @param newPassword New password
     * @return Flow emitting reset status
     */
    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): Flow<ApiResponse<Unit>>

    /**
     * Profile information
     * @return ApiResponse containing profile data
     */
    suspend fun profile(): Flow<ApiResponse<ProfileResponse>>

    /**
     * Person image
     * @param fileName Name of the file
     * @param expiryHours Expiry hours for the image
     * @return ApiResponse containing person image data
     */
    suspend fun personImage(fileName: String, expiryHours: Int): Flow<ApiResponse<PersonImageResponse>>

    /**
     * Save authentication token locally
     * @param token Access token
     * @param refreshToken Refresh token
     * @param fullName User's full name
     * @param personalPhotoPath User's personal photo path
     */
    suspend fun saveAuthToken(token: String, refreshToken: String?, fullName: String?, personalPhotoPath: String?)

    /**
     * Get saved access token
     * @return Saved access token or null
     */
    suspend fun getAccessToken(): String?

    /**
     * Get saved refresh token
     * @return Saved refresh token or null
     */
    suspend fun getRefreshToken(): String?

    /**
     * Clear all saved authentication data
     */
    suspend fun clearAuthData()

    /**
     * Check if user is logged in
     * @return True if user has valid token
     */
    suspend fun isLoggedIn(): Boolean
}

