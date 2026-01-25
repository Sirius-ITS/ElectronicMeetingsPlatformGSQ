package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable

@Serializable
data class AllMeetingDetailResponse(
    val data: MeetingDetail,
    val message: String,
    val success: Boolean
)