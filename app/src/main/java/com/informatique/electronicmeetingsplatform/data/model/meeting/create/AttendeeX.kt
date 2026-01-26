package com.informatique.electronicmeetingsplatform.data.model.meeting.create

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeX(
    val departmentName: String,
    val email: String,
    val fullName: String,
    val isOrganizer: Boolean,
    val jobName: String,
    val otherRefuseReason: String? = null,
    val personId: Int,
    val personalPhotoPath: String,
    val qid: String,
    val refuseReason: String? = null,
    val refuseReasonId: String? = null,
    val status: String
)