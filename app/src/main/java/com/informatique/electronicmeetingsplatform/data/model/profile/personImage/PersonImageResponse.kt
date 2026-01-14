package com.informatique.electronicmeetingsplatform.data.model.profile.personImage

import kotlinx.serialization.Serializable

@Serializable
data class PersonImageResponse(
    val expiresAt: String,
    val message: String,
    val metadata: Metadata,
    val success: Boolean,
    val url: String
)