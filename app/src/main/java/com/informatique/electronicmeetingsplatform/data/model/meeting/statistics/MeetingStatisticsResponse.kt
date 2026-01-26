package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.Serializable

@Serializable
data class MeetingStatisticsResponse(
    val data: Data,
    val message: String,
    val success: Boolean
)