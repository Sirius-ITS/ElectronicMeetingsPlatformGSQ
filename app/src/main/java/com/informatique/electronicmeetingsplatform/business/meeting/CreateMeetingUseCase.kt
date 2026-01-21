package com.informatique.electronicmeetingsplatform.business.meeting

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.Attendee
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.CreateMeetingRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.CreateMeetingResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepository
import javax.inject.Inject
import kotlin.String
import kotlin.collections.List

/**
 * Login use case following Clean Architecture principles
 * Handles business logic for user authentication
 */
class CreateMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCase<CreateMeetingUseCase.Params, CreateMeetingResponse>() {

    /**
     * Parameters for login operation
     * @property attachmentPaths List of attachment paths
     * @property attendees List of attendees
     * @property dateFrom start data
     * @property dateTo end date
     * @property isOffical official meeting
     * @property isPersonal personal meeting
     * @property isRepeated repeated meeting
     * @property location meeting's location
     * @property meetingPriorityId meeting's priority ID
     * @property meetingTypeId meeting's type ID
     * @property notes meeting's note
     * @property repeatRule meeting's repeat rule
     * @property timeFrom start time
     * @property timeTo end time
     * @property topic meeting's topic
     */
    data class Params(
        val attachmentPaths: List<String>?,
        val externalAttendees: List<Attendee>?,
        val attendees: List<Attendee>,
        val dateFrom: String,
        val dateTo: String,
        val isPersonal: Boolean,
        val isRepeated: Boolean?,
        val location: String,
        val meetingPriorityId: Int,
        val meetingTypeId: Int,
        val notes: String?,
        val repeatRule: String?,
        val timeFrom: String,
        val timeTo: String,
        val topic: String
    )

    override suspend fun invoke(parameters: Params): BusinessState<CreateMeetingResponse> {
        // Validate input parameters
        val validationError = validateParameters(parameters)
        if (validationError != null) {
            return BusinessState.Error(validationError)
        }

        // Execute create meeting - collect flow and get the last emission (skip Loading)
        var finalResponse: ApiResponse<CreateMeetingResponse> = ApiResponse.Loading

        meetingRepository.createMeeting(
            request = CreateMeetingRequest(
                attachmentPaths = parameters.attachmentPaths,
                externalAttendees = parameters.externalAttendees,
                attendees = parameters.attendees,
                dateFrom = parameters.dateFrom,
                dateTo = parameters.dateTo,
                isPersonal = parameters.isPersonal,
                isRepeated = parameters.isRepeated,
                location = parameters.location,
                meetingPriorityId = parameters.meetingPriorityId,
                meetingTypeId = parameters.meetingTypeId,
                notes = parameters.notes,
                repeatRule = parameters.repeatRule,
                timeFrom = parameters.timeFrom,
                timeTo = parameters.timeTo,
                topic = parameters.topic
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
            params.topic.isBlank() -> "Topic cannot be empty"
            params.location.isBlank() -> "Location cannot be empty"
            params.dateFrom.isBlank() -> "dateFrom cannot be empty"
            params.dateTo.isBlank() -> "dateTo cannot be empty"
            else -> null
        }
    }
}


