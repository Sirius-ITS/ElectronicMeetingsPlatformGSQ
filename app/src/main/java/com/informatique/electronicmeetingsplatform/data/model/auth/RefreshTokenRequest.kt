package com.informatique.electronicmeetingsplatform.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Refresh token request for obtaining new access token
 */
@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("grant_type")
    val grantType: String = "refresh_token"
)

