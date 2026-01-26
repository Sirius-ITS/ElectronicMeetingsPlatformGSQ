package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable

@Serializable
data class RespondMeetingResponse(
    val data: Meeting,
    val message: String,
    val success: Boolean
)
