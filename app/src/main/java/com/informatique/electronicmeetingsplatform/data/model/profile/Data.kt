package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val careerSteps: List<CareerStep>,
    val departmentId: Int,
    val departmentName: String,
    val email: String,
    val expiresAt: String,
    val familyMembers: List<FamilyMember>,
    val fullName: String,
    val person: Person,
    val qualifications: List<Qualification>,
    val reportsToPersonFullName: String? = null,
    val reportsToPersonId: Int? = null,
    val reportsToUserId: String? = null,
    val roles: List<String>,
    val sectorId: Int? = null,
    val sectorName: String,
    val serviceRoles: Map<String, String> = emptyMap(),
    val token: String,
    val userId: String,
    val userName: String
)