package com.informatique.electronicmeetingsplatform.data.model.meeting.type

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val id: Int,
    val isSpecial: Boolean,
    val name: String
)