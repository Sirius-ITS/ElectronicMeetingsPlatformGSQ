package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class FamilyMember(
    val createdAt: String,
    val dateOfBirth: String? = null,
    val firstName: String,
    val fullName: String,
    val gender: String? = null,
    val id: Int,
    val lastName: String,
    val nationality: String? = null,
    val personId: Int,
    val relationshipTypeId: Int
)