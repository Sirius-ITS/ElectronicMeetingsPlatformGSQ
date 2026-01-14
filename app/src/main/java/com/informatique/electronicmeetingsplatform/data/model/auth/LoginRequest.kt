package com.informatique.electronicmeetingsplatform.data.model.auth

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginRequest(

    @SerialName("Email")
    val email: String,

    @SerialName("Password")
    val password: String
)


