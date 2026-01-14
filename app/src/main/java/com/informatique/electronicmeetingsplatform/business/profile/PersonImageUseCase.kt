package com.informatique.electronicmeetingsplatform.business.profile

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.model.profile.personImage.PersonImageResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepository
import javax.inject.Inject

/**
 * Person image use case following Clean Architecture principles
 * Handles business logic for user authentication
 */
class PersonImageUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : BaseUseCase<PersonImageUseCase.Params, PersonImageResponse>() {

    /**
     * Parameters for person image operation
     * @property fileName Name of the file
     * @property expiryHours Expiry hours for the image
     */
    data class Params(
        val fileName: String,
        val expiryHours: Int
    )

    override suspend fun invoke(parameters: Params): BusinessState<PersonImageResponse> {
        // Validate input parameters
        val validationError = validateParameters(parameters)
        if (validationError != null) {
            return BusinessState.Error(validationError)
        }

        // Execute person image - collect flow and get the last emission (skip Loading)
        var finalResponse: ApiResponse<PersonImageResponse> = ApiResponse.Loading

        authRepository.personImage(
            fileName = parameters.fileName.trim(),
            expiryHours = parameters.expiryHours
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
            params.fileName.isBlank() -> "Username cannot be empty"
            else -> null
        }
    }

}


