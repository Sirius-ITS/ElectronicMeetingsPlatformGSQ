package com.informatique.electronicmeetingsplatform.data.model.meeting.create

import kotlinx.serialization.Serializable

@Serializable
data class Attendee(
    val departmentDescription: String? = "",
    val email: String? = null,
    val fullName: String? = null,
    val id: Int? = null,
    val jobDescription: String? = "",
    val personalPhotoPath: String? = null,
    val qid: String? = null
)