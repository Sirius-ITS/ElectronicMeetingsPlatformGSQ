package com.informatique.electronicmeetingsplatform.business.login

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.model.auth.LoginResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepository
import javax.inject.Inject

/**
 * Login use case following Clean Architecture principles
 * Handles business logic for user authentication
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : BaseUseCase<LoginUseCase.Params, LoginResponse>() {

    /**
     * Parameters for login operation
     * @property username User's username or email
     * @property password User's password
     */
    data class Params(
        val username: String,
        val password: String
    )

    override suspend fun invoke(parameters: Params): BusinessState<LoginResponse> {
        // Validate input parameters
        val validationError = validateParameters(parameters)
        if (validationError != null) {
            return BusinessState.Error(validationError)
        }

        // Execute login - collect flow and get the last emission (skip Loading)
        var finalResponse: ApiResponse<LoginResponse> = ApiResponse.Loading

        authRepository.login(
            email = parameters.username.trim(),
            password = parameters.password
        ).collect { response ->
            // Skip Loading state, wait for actual response
            if (response !is ApiResponse.Loading) {
                finalResponse = response
            }
        }

        // Extract the final response to a local val for smart cast
        return when (val result = finalResponse) {
            is ApiResponse.Success -> {
                BusinessState.Success(result.data)
            }
            is ApiResponse.Error -> {
                BusinessState.Error(result.message)
            }
            is ApiResponse.NetworkError -> {
                BusinessState.Error("Network error. Please check your connection.")
            }
            is ApiResponse.Loading -> {
                // This shouldn't happen, but handle it just in case
                BusinessState.Loading
            }
        }
    }

    /**
     * Validate input parameters before making API call
     * @return Error message if validation fails, null otherwise
     */
    private fun validateParameters(params: Params): String? {
        return when {
            params.username.isBlank() -> "Username cannot be empty"
            params.password.isBlank() -> "Password cannot be empty"
            params.password.length < 6 -> "Password must be at least 6 characters"
            params.username.length < 3 -> "Username must be at least 3 characters"
            else -> null
        }
    }
}


