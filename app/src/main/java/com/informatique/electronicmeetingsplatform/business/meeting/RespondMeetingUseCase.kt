package com.informatique.electronicmeetingsplatform.business.meeting

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.RespondMeetingRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.RespondMeetingResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepository
import javax.inject.Inject
import kotlin.String

/**
 * Login use case following Clean Architecture principles
 * Handles business logic for user authentication
 */
class RespondMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCase<RespondMeetingUseCase.Params, RespondMeetingResponse>() {

    /**
     * Parameters for login operation
     * @property meetingId The id of meeting to respond
     * @property response The response to the meeting ("Accept", "Refuse")
     * @property reasonId The id of the reason for the response (optional)
     * @property otherReason The other reason for the response (optional)
     */
    data class Params(
        val meetingId: Int,
        val response: String,
        val reasonId: Int?,
        val otherReason: String?
    )

    override suspend fun invoke(parameters: Params): BusinessState<RespondMeetingResponse> {
        // Validate input parameters
        val validationError = validateParameters(parameters)
        if (validationError != null) {
            return BusinessState.Error(validationError)
        }

        // Execute create meeting - collect flow and get the last emission (skip Loading)
        var finalResponse: ApiResponse<RespondMeetingResponse> = ApiResponse.Loading

        meetingRepository.meetingRespondStatus(
            respond = RespondMeetingRequest(
                meetingId = parameters.meetingId,
                response = parameters.response,
                reasonId = parameters.reasonId,
                otherReason = parameters.otherReason
            ),
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
            params.meetingId == 0 -> "Meeting id cannot be empty"
            params.response.isBlank() -> "Response cannot be empty"
            else -> null
        }
    }
}


