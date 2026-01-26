package com.informatique.electronicmeetingsplatform.data.model.meeting.create

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeX(
    val departmentName: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val isOrganizer: Boolean? = null,
    val jobName: String? = null,
    val otherRefuseReason: String? = null,
    val personId: Int? = null,
    val personalPhotoPath: String? = null,
    val qid: String? = null,
    val refuseReason: String? = null,
    val refuseReasonId: String? = null,
    val status: String? = null
)