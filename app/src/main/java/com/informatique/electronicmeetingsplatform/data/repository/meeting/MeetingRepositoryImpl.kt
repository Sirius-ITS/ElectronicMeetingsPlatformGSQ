package com.informatique.electronicmeetingsplatform.data.repository.meeting

import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingDetailResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.RespondMeetingRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.RespondMeetingResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.DeleteAttachmentRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.DeleteAttachmentResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.CreateMeetingRequest
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.CreateMeetingResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.invitees.InviteesResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.priorities.MeetingPrioritiesResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.statistics.MeetingStatisticsResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.type.MeetingTypeResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.remote.meeting.MeetingApiService
import com.informatique.electronicmeetingsplatform.data.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeetingRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val meetingApiService: MeetingApiService
) : MeetingRepository {

    override suspend fun meetingStatistics(): Flow<ApiResponse<MeetingStatisticsResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.meetingStatistics()

        // Check for session expiration
        checkSessionExpiration(response)

        // If login successful, save tokens
        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun meetingTypes(): Flow<ApiResponse<MeetingTypeResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.meetingTypes()

        // Check for session expiration
        checkSessionExpiration(response)

        // If login successful, save tokens
        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun meetingPriorities(): Flow<ApiResponse<MeetingPrioritiesResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.meetingPriorities()

        // Check for session expiration
        checkSessionExpiration(response)

        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun meetingInvitees(): Flow<ApiResponse<InviteesResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.meetingInvitees()

        // Check for session expiration
        checkSessionExpiration(response)

        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun meetingAttachments(request: AttachmentRequest):
            Flow<ApiResponse<AttachmentResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.meetingAttachments(request)

        // Check for session expiration
        checkSessionExpiration(response)

        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun deleteAttachment(request: DeleteAttachmentRequest):
            Flow<ApiResponse<DeleteAttachmentResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.deleteAttachment(request)

        // Check for session expiration
        checkSessionExpiration(response)

        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun createMeeting(request: CreateMeetingRequest):
            Flow<ApiResponse<CreateMeetingResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.createMeeting(request)

        // Check for session expiration
        checkSessionExpiration(response)

        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun allMeetings(): Flow<ApiResponse<AllMeetingResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.allMeetings()

        // Check for session expiration
        checkSessionExpiration(response)

        // If login successful, save tokens
        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun allMeetingDetail(meetingId: Int): Flow<ApiResponse<AllMeetingDetailResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.allMeetingDetail(meetingId)

        // Check for session expiration
        checkSessionExpiration(response)

        // If login successful, save tokens
        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    override suspend fun meetingRespondStatus(respond: RespondMeetingRequest):
            Flow<ApiResponse<RespondMeetingResponse>> = flow {
        emit(ApiResponse.Loading)

        val response = meetingApiService.meetingRespondStatus(respond)

        // Check for session expiration
        checkSessionExpiration(response)

        when (response) {
            is ApiResponse.Success -> {}
            else -> { /* No action needed */ }
        }

        emit(response)
    }

    /**
     * Check if response indicates session expiration and trigger alert
     */
    private fun checkSessionExpiration(response: ApiResponse<*>) {
        if (response is ApiResponse.Error && response.code == 401) {
            sessionManager.triggerSessionExpired()
        }
    }
}

