package com.informatique.electronicmeetingsplatform.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("token")
    val accessToken: String,
    val expiresAt: String,
    val userId: String,
    val userName: String,
    val email: String,
    val fullName: String,
    val departmentId: Int?,
    val departmentName: String?,
    val sectorId: Int? = null,
    val sectorName: String? = null,
    val roles: List<String> = emptyList(),
    val serviceRoles: Map<String, String> = emptyMap(),
    val services: List<Service> = emptyList(),
    val refreshToken: String,
    val refreshTokenExpiresAt: String,
    @SerialName("sipURL")
    val sipUrl: String? = null,
    val sipUserName: String? = null,
    val sipUserPassword: String? = null,
    val mdmMobileNumber: String? = null,
    val isDepartmentManager: Boolean,
    val personalPhotoPath: String? = null,
    val personDepartmentJobs: List<PersonDepartmentJob> = emptyList()
) {
    // Helper property to get personalPhotoPath from first active job
    val userPhotoPath: String?
        get() = personDepartmentJobs.firstOrNull()?.personalPhotoPath
}

@Serializable
data class PersonDepartmentJob(
    val id: Int,
    val personId: Int,
    val personName: String,
    val departmentJobId: Int,
    val departmentId: Int,
    val departmentName: String,
    val jobId: Int,
    val jobTitle: String,
    val startDate: String,
    val endDate: String? = null,
    val isActive: Boolean,
    val sectorId: Int? = null,
    val sectorName: String? = null,
    val personalPhotoPath: String? = null,
    val createdAt: String
)

@Serializable
data class Service(
    val id: Int,
    val name: String,
    val code: String,
    val description: String,
    val url: String? = null,
    val photoPath: String? = null,
    val isActive: Boolean,
    val isPublic: Boolean,
    val requiresAuthentication: Boolean,
    val order: Int
)

