package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class Telephone(
    val createdAt: String,
    val description: String,
    val id: Int,
    val number: String,
    val personId: Int,
    val telephoneTypeId: Int,
    val telephoneTypeName: String
)