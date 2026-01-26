package com.informatique.electronicmeetingsplatform.data.repository.meeting

import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingDetailResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingResponse
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
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    suspend fun meetingStatistics(): Flow<ApiResponse<MeetingStatisticsResponse>>

    /**
     * Meeting types lookup
     * @return Flow emitting meeting response states
     */
    suspend fun meetingTypes(): Flow<ApiResponse<MeetingTypeResponse>>

    suspend fun meetingPriorities(): Flow<ApiResponse<MeetingPrioritiesResponse>>

    suspend fun meetingInvitees(): Flow<ApiResponse<InviteesResponse>>

    suspend fun meetingAttachments(request: AttachmentRequest): Flow<ApiResponse<AttachmentResponse>>

    suspend fun deleteAttachment(request: DeleteAttachmentRequest): Flow<ApiResponse<DeleteAttachmentResponse>>

    suspend fun createMeeting(request: CreateMeetingRequest): Flow<ApiResponse<CreateMeetingResponse>>

    suspend fun allMeetings(): Flow<ApiResponse<AllMeetingResponse>>

    suspend fun allMeetingDetail(meetingId: Int): Flow<ApiResponse<AllMeetingDetailResponse>>


}

