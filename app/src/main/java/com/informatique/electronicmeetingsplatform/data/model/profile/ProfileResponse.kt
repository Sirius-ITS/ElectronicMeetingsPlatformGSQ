package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: Data,
    val errors: Map<String, String>? = null,
    val statusCode: Int? = null
)