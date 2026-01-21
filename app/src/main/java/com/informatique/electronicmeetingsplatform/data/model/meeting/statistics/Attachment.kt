package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val filePath: String,
    val id: Int
)