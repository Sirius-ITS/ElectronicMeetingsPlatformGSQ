package com.informatique.electronicmeetingsplatform.data.remote.auth

import com.informatique.electronicmeetingsplatform.data.model.auth.LoginRequest
import com.informatique.electronicmeetingsplatform.data.model.auth.LoginResponse
import com.informatique.electronicmeetingsplatform.data.model.auth.LogoutRequest
import com.informatique.electronicmeetingsplatform.data.model.auth.RefreshTokenRequest
import com.informatique.electronicmeetingsplatform.data.model.profile.ProfileResponse
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageRequest
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse

/**
 * Authentication API service interface
 * Defines all authentication-related network operations
 */
interface AuthApiService {

    /**
     * Authenticate user with credentials
     * @param request Login credentials
     * @return ApiResponse containing login result
     */
    suspend fun login(request: LoginRequest): ApiResponse<LoginResponse>

    /**
     * Refresh access token using refresh token
     * @param request Refresh token request
     * @return ApiResponse containing new tokens
     */
    suspend fun refreshToken(request: RefreshTokenRequest): ApiResponse<LoginResponse>

    /**
     * Logout user and invalidate tokens
     * @param request Logout request with optional parameters
     * @return ApiResponse indicating logout success
     */
    suspend fun logout(request: LogoutRequest): ApiResponse<Unit>

    /**
     * Verify if current session is valid
     * @return ApiResponse indicating session validity
     */
    suspend fun verifySession(): ApiResponse<Boolean>

    /**
     * Request password reset
     * @param email User's email address
     * @return ApiResponse indicating request status
     */
    suspend fun requestPasswordReset(email: String): ApiResponse<Unit>

    /**
     * Verify password reset code
     * @param email User's email
     * @param code Reset code
     * @return ApiResponse indicating verification status
     */
    suspend fun verifyResetCode(email: String, code: String): ApiResponse<Boolean>

    /**
     * Reset password with verified code
     * @param email User's email
     * @param code Reset code
     * @param newPassword New password
     * @return ApiResponse indicating reset status
     */
    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): ApiResponse<Unit>

    /**
     * Profile information
     * @return ApiResponse containing profile data
     */
    suspend fun profile(): ApiResponse<ProfileResponse>

    /**
     * Person image
     * @return ApiResponse containing person image data
     */
    suspend fun personImage(requestData: PersonImageRequest): ApiResponse<PersonImageResponse>
}


