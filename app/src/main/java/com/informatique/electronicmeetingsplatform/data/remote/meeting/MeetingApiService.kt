package com.informatique.electronicmeetingsplatform.data.remote.meeting

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

/**
 * Meetings API service interface
 * Defines all authentication-related network operations
 */
interface MeetingApiService {

    suspend fun meetingStatistics(): ApiResponse<MeetingStatisticsResponse>

    /**
     * Meeting type
     * @return ApiResponse containing login result
     */
    suspend fun meetingTypes(): ApiResponse<MeetingTypeResponse>

    suspend fun meetingPriorities(): ApiResponse<MeetingPrioritiesResponse>

    suspend fun meetingInvitees(): ApiResponse<InviteesResponse>

    suspend fun meetingAttachments(request: AttachmentRequest): ApiResponse<AttachmentResponse>

    suspend fun deleteAttachment(request: DeleteAttachmentRequest): ApiResponse<DeleteAttachmentResponse>

    suspend fun createMeeting(request: CreateMeetingRequest): ApiResponse<CreateMeetingResponse>

    suspend fun allMeetings(): ApiResponse<AllMeetingResponse>

    suspend fun allMeetingDetail(meetingId: Int): ApiResponse<AllMeetingDetailResponse>

}


