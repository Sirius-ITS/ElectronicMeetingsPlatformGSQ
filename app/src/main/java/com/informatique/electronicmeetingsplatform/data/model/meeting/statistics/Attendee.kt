package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Attendee(
    val departmentName: String,
    val email: String? = null,
    val fullName: String,
    val isOrganizer: Boolean,
    val jobName: String,
    val personId: Int,
    val personalPhotoPath: String? = null,
    val qid: String? = null,
    val refuseReason: String? = null,
    val rejectionReason: RejectionReason? = null,
    val status: String
)