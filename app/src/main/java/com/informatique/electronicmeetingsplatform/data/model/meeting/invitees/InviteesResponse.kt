package com.informatique.electronicmeetingsplatform.data.model.meeting.invitees

import kotlinx.serialization.Serializable

@Serializable
data class InviteesResponse(
    val data: Data,
    val errors: Map<String, String>? = null,
    val message: String,
    val statusCode: Int,
    val success: Boolean
)