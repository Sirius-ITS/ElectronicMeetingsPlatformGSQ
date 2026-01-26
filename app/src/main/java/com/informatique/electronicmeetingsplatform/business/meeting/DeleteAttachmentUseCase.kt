package com.informatique.electronicmeetingsplatform.business.meeting

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.DeleteAttachmentRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.DeleteAttachmentResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepository
import javax.inject.Inject
import kotlin.String

/**
 * Login use case following Clean Architecture principles
 * Handles business logic for user authentication
 */
class DeleteAttachmentUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCase<DeleteAttachmentUseCase.Params, DeleteAttachmentResponse>() {

    /**
     * Parameters for login operation
     * @property fileName attachment file name
     */
    data class Params(
        val fileName: String
    )

    override suspend fun invoke(parameters: Params): BusinessState<DeleteAttachmentResponse> {
        // Validate input parameters
        val validationError = validateParameters(parameters)
        if (validationError != null) {
            return BusinessState.Error(validationError)
        }

        // Execute create meeting - collect flow and get the last emission (skip Loading)
        var finalResponse: ApiResponse<DeleteAttachmentResponse> = ApiResponse.Loading

        meetingRepository.deleteAttachment(
            request = DeleteAttachmentRequest(
                fileName = parameters.fileName
            )
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
            params.fileName.isBlank() -> "File name cannot be empty"
            else -> null
        }
    }
}


