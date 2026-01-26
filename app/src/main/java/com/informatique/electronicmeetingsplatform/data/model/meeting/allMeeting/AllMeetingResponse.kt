package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable

@Serializable
data class AllMeetingResponse(
    val data: Data,
    val message: String,
    val success: Boolean
)