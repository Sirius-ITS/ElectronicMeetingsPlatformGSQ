package com.informatique.electronicmeetingsplatform.business.login

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepository
import javax.inject.Inject

/**
 * Verify session use case
 * Checks if current session is valid
 */
class VerifySessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : BaseUseCase<Unit, Boolean>() {

    override suspend fun invoke(parameters: Unit): BusinessState<Boolean> {
        var finalResponse: ApiResponse<Boolean> = ApiResponse.Loading

        authRepository.verifySession().collect { response ->
            if (response !is ApiResponse.Loading) {
                finalResponse = response
            }
        }

        val result = finalResponse
        return when (result) {
            is ApiResponse.Success -> {
                BusinessState.Success(result.data)
            }
            is ApiResponse.Error -> {
                BusinessState.Success(false)
            }
            is ApiResponse.NetworkError -> {
                // If network error, check local token existence
                val isLoggedIn = authRepository.isLoggedIn()
                BusinessState.Success(isLoggedIn)
            }
            is ApiResponse.Loading -> {
                BusinessState.Loading
            }
        }
    }
}

