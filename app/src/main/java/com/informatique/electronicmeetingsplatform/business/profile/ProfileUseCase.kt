package com.informatique.electronicmeetingsplatform.business.profile

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCaseWithoutParams
import com.informatique.electronicmeetingsplatform.data.model.profile.ProfileResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepository
import javax.inject.Inject

/**
 * Profile use case following Clean Architecture principles
 * Handles business logic for user authentication
 */
class ProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : BaseUseCaseWithoutParams<ProfileResponse>() {

    override suspend fun invoke(): BusinessState<ProfileResponse> {

        // Execute login - collect flow and get the last emission (skip Loading)
        var finalResponse: ApiResponse<ProfileResponse> = ApiResponse.Loading

        authRepository.profile().collect { response ->
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

}


