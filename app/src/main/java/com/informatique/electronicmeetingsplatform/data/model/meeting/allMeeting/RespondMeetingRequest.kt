package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable

@Serializable
data class RespondMeetingRequest(
    val meetingId: Int,
    val response: String,
    val reasonId: Int?,
    val otherReason: String?
)
