package com.informatique.electronicmeetingsplatform.data.model.meeting.type

import kotlinx.serialization.Serializable

@Serializable
data class MeetingTypeResponse(
    val data: List<Data>,
    val message: String,
    val success: Boolean
)