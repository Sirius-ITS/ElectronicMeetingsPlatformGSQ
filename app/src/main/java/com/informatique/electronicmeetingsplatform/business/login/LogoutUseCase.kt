package com.informatique.electronicmeetingsplatform.business.login

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepository
import javax.inject.Inject

/**
 * Logout use case
 * Handles business logic for user logout
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : BaseUseCase<LogoutUseCase.Params, Unit>() {

    /**
     * Parameters for logout operation
     * @property allDevices If true, logout from all devices
     */
    data class Params(
        val allDevices: Boolean = false
    )

    override suspend fun invoke(parameters: Params): BusinessState<Unit> {
        var finalResponse: ApiResponse<Unit> = ApiResponse.Loading

        authRepository.logout(parameters.allDevices).collect { response ->
            if (response !is ApiResponse.Loading) {
                finalResponse = response
            }
        }

        // Even if logout API fails, we still cleared local tokens, so return success
        return BusinessState.Success(Unit)
    }
}

