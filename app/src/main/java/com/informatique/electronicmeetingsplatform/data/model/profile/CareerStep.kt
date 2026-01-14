package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class CareerStep(
    val affiliatedEntityName: String? = null,
    val createdAt: String,
    val id: Int,
    val personId: Int,
    val positionDate: String? = null,
    val positionName: String? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null
)