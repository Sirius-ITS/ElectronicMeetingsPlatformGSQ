package com.informatique.electronicmeetingsplatform.data.model.meeting.create

import kotlinx.serialization.Serializable

@Serializable
data class CreateMeetingResponse(
    val data: Data,
    val message: String,
    val success: Boolean
)