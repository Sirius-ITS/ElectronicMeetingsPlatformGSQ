package com.informatique.electronicmeetingsplatform.data.model.meeting.priorities

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val id: Int,
    val name: String,
    val order: Int
)