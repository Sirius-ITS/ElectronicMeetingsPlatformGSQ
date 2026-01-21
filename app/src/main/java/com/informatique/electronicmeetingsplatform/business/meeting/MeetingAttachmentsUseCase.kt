package com.informatique.electronicmeetingsplatform.business.meeting

import android.net.Uri
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepository
import java.io.File
import javax.inject.Inject
import kotlin.String

/**
 * Login use case following Clean Architecture principles
 * Handles business logic for user authentication
 */
class MeetingAttachmentsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCase<MeetingAttachmentsUseCase.Params, AttachmentResponse>() {

    /**
     * Parameters for login operation
     * @property file Attachment file uri (pdf / image / audio / video)
     * @property fileName Attachment file name
     */
    data class Params(
        val fileName: String,
        val file: Uri?
    )

    override suspend fun invoke(parameters: Params): BusinessState<AttachmentResponse> {
        // Validate input parameters
        val validationError = validateParameters(parameters)
        if (validationError != null) {
            return BusinessState.Error(validationError)
        }

        // Execute create meeting - collect flow and get the last emission (skip Loading)
        var finalResponse: ApiResponse<AttachmentResponse> = ApiResponse.Loading

        meetingRepository.meetingAttachments(
            request = AttachmentRequest(fileName = parameters.fileName, file = parameters.file!!)
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
            params.fileName.isEmpty() -> "File name cannot be empty"
            params.file == null -> "File cannot be empty"
            else -> null
        }
    }
}


