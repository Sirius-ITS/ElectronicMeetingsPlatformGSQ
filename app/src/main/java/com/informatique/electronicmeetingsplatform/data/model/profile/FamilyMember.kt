package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class FamilyMember(
    val createdAt: String,
    val dateOfBirth: String,
    val firstName: String,
    val fullName: String,
    val gender: String,
    val id: Int,
    val lastName: String,
    val nationality: String,
    val personId: Int,
    val relationshipTypeId: Int
)