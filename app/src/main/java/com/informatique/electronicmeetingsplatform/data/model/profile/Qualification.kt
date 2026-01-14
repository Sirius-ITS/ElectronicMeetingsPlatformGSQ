package com.informatique.electronicmeetingsplatform.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class Qualification(
    val certificateName: String,
    val country: String,
    val createdAt: String,
    val departmentName: String,
    val educationLevel: String,
    val grade: String,
    val id: Int,
    val institutionName: String,
    val major: String,
    val personId: Int,
    val qualificationDate: String
)