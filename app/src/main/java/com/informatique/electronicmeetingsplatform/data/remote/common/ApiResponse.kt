package com.informatique.electronicmeetingsplatform.data.remote.common

/**
 * Sealed class representing API response states
 * Provides type-safe error handling for network operations
 */
sealed class ApiResponse<out T> {
    /**
     * Successful response with data
     */
    data class Success<T>(val data: T) : ApiResponse<T>()

    /**
     * Error response with details
     */
    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : ApiResponse<Nothing>()

    /**
     * Network error (no connection, timeout, etc.)
     */
    data class NetworkError(
        val exception: Throwable
    ) : ApiResponse<Nothing>()

    /**
     * Loading state
     */
    object Loading : ApiResponse<Nothing>()

    /**
     * Check if response is successful
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Check if response is error
     */
    fun isError(): Boolean = this is Error || this is NetworkError

    /**
     * Get data or null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Get error message or null
     */
    fun getErrorMessage(): String? = when (this) {
        is Error -> message
        is NetworkError -> exception.message ?: "Network error occurred"
        else -> null
    }
}

