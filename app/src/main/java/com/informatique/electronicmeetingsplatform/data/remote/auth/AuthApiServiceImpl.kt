package com.informatique.electronicmeetingsplatform.data.remote.auth

import com.informatique.electronicmeetingsplatform.data.model.auth.LoginRequest
import com.informatique.electronicmeetingsplatform.data.model.auth.LoginResponse
import com.informatique.electronicmeetingsplatform.data.model.auth.LogoutRequest
import com.informatique.electronicmeetingsplatform.data.model.auth.RefreshTokenRequest
import com.informatique.electronicmeetingsplatform.data.model.profile.ProfileResponse
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageRequest
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.di.module.AppRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthApiService using Ktor HttpClient
 * Handles all authentication network requests with advanced error handling
 */
@Singleton
class AuthApiServiceImpl @Inject constructor(
    private val appRepository: AppRepository,
    private val json: Json
) : AuthApiService {

    companion object {
        private const val AUTH_BASE_PATH = "/api/v1/auth"
        private const val LOGIN_ENDPOINT = "api/identity/v1/auth/login"

        private const val PROFILE_ENDPOINT = "api/identity/v1/mobile/auth/me"
        private const val PERSONAL_IMAGE_ENDPOINT = "api/media/mobile/presigned-url"
        private const val REFRESH_ENDPOINT = "$AUTH_BASE_PATH/refresh"
        private const val LOGOUT_ENDPOINT = "$AUTH_BASE_PATH/logout"
        private const val VERIFY_SESSION_ENDPOINT = "$AUTH_BASE_PATH/verify"
        private const val PASSWORD_RESET_REQUEST_ENDPOINT = "$AUTH_BASE_PATH/password/reset/request"
        private const val PASSWORD_RESET_VERIFY_ENDPOINT = "$AUTH_BASE_PATH/password/reset/verify"
        private const val PASSWORD_RESET_ENDPOINT = "$AUTH_BASE_PATH/password/reset"
    }

    override suspend fun login(request: LoginRequest): ApiResponse<LoginResponse> {
        return when (val response = appRepository.onPost(LOGIN_ENDPOINT, request)) {
            is ApiResponse.Success -> {
                ApiResponse.Success(
                    json.decodeFromJsonElement(
                        LoginResponse.serializer(), response.data))
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Login failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): ApiResponse<LoginResponse> {
        return safeApiCall {
            val response = appRepository.client.post(REFRESH_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status.value) {
                in 200..299 -> {
                    val loginResponse = response.body<LoginResponse>()
                    ApiResponse.Success(loginResponse)
                }
                401 -> {
                    ApiResponse.Error(
                        message = "Refresh token expired. Please login again.",
                        code = 401
                    )
                }
                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Token refresh failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    override suspend fun logout(request: LogoutRequest): ApiResponse<Unit> {
        return safeApiCall {
            val response = appRepository.client.post(LOGOUT_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status.value) {
                in 200..299 -> ApiResponse.Success(Unit)
                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Logout failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    override suspend fun verifySession(): ApiResponse<Boolean> {
        return safeApiCall {
            val response = appRepository.client.post(VERIFY_SESSION_ENDPOINT) {
                contentType(ContentType.Application.Json)
            }

            when (response.status.value) {
                200 -> ApiResponse.Success(true)
                401 -> ApiResponse.Success(false)
                else -> ApiResponse.Error(
                    message = "Session verification failed",
                    code = response.status.value
                )
            }
        }
    }

    override suspend fun requestPasswordReset(email: String): ApiResponse<Unit> {
        return safeApiCall {
            val response = appRepository.client.post(PASSWORD_RESET_REQUEST_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }

            when (response.status.value) {
                in 200..299 -> ApiResponse.Success(Unit)
                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Password reset request failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    override suspend fun verifyResetCode(email: String, code: String): ApiResponse<Boolean> {
        return safeApiCall {
            val response = appRepository.client.post(PASSWORD_RESET_VERIFY_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "code" to code))
            }

            when (response.status.value) {
                200 -> ApiResponse.Success(true)
                400, 401 -> ApiResponse.Success(false)
                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Code verification failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    override suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): ApiResponse<Unit> {
        return safeApiCall {
            val response = appRepository.client.post(PASSWORD_RESET_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "email" to email,
                        "code" to code,
                        "new_password" to newPassword
                    )
                )
            }

            when (response.status.value) {
                in 200..299 -> ApiResponse.Success(Unit)
                else -> {
                    val errorBody = runCatching { response.body<ErrorResponse>() }.getOrNull()
                    ApiResponse.Error(
                        message = errorBody?.message ?: "Password reset failed",
                        code = response.status.value
                    )
                }
            }
        }
    }

    override suspend fun profile(): ApiResponse<ProfileResponse> {
        return when (val response = appRepository.onGet(PROFILE_ENDPOINT)) {
            is ApiResponse.Success -> {
                ApiResponse.Success(
                    json.decodeFromJsonElement(
                        ProfileResponse.serializer(),
                        response.data
                    )
                )
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Profile failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    override suspend fun personImage(requestData: PersonImageRequest): ApiResponse<PersonImageResponse> {
        return when (val response = appRepository.onPost(PERSONAL_IMAGE_ENDPOINT, requestData)) {
            is ApiResponse.Success -> {
                ApiResponse.Success(
                    json.decodeFromJsonElement(
                        PersonImageResponse.serializer(),
                        response.data
                    )
                )
            }

            else -> {
                val errorBody = when (response) {
                    is ApiResponse.Error -> null
                    is ApiResponse.NetworkError -> null
                    else -> null
                }
                ApiResponse.Error(
                    message = errorBody ?: "Person image failed",
                    code = when (response) {
                        is ApiResponse.Error -> response.code
                        else -> -1
                    }
                )
            }
        }
    }

    /**
     * Safe API call wrapper with comprehensive error handling
     * Catches and transforms all possible exceptions into ApiResponse types
     */
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> ApiResponse<T>
    ): ApiResponse<T> {
        return try {
            apiCall()
        } catch (e: ClientRequestException) {
            // 4xx errors
            ApiResponse.Error(
                message = e.response.body<ErrorResponse>().message ?: "Client error: ${e.message}",
                code = e.response.status.value,
                exception = e
            )
        } catch (e: ServerResponseException) {
            // 5xx errors
            ApiResponse.Error(
                message = "Server error: ${e.message}",
                code = e.response.status.value,
                exception = e
            )
        } catch (e: IOException) {
            // Network errors
            ApiResponse.NetworkError(e)
        } catch (e: CancellationException) {
            // Coroutine cancellation - rethrow
            throw e
        } catch (e: Exception) {
            // Generic errors
            ApiResponse.Error(
                message = e.message ?: "Unknown error occurred",
                exception = e
            )
        }
    }

    /**
     * Standard error response structure
     */
    @Serializable
    private data class ErrorResponse(
        @SerialName("message")
        val message: String? = null,

        @SerialName("error")
        val error: String? = null,

        @SerialName("errors")
        val errors: Map<String, List<String>>? = null
    )
}

