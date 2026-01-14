package com.informatique.electronicmeetingsplatform.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Logout request to invalidate tokens
 */
@Serializable
data class LogoutRequest(
    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("all_devices")
    val allDevices: Boolean = false
)

