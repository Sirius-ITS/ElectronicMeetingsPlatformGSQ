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
    val departmentId: Int,
    val departmentName: String,
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
)

@Serializable
data class Service(
    val id: Int,
    val name: String,
    val code: String,
    val description: String,
    val url: String,
    val photoPath: String? = null,
    val isActive: Boolean,
    val isPublic: Boolean,
    val requiresAuthentication: Boolean,
    val order: Int
)

