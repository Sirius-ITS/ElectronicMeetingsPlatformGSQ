package com.informatique.electronicmeetingsplatform.data.model.meeting.priorities

import kotlinx.serialization.Serializable

@Serializable
data class MeetingPrioritiesResponse(
    val data: List<Data>,
    val message: String,
    val success: Boolean
)