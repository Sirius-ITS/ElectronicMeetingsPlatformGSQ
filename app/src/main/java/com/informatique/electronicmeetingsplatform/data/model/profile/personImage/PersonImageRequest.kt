package com.informatique.electronicmeetingsplatform.data.model.profile.personImage

import kotlinx.serialization.Serializable

@Serializable
data class PersonImageRequest(
    val fileName: String,
    val expiryHours: Int = 24
)
