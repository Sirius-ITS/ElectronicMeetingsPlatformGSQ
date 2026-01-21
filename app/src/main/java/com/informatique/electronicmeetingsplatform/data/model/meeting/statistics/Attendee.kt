package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Attendee(
    val departmentName: String,
    val email: String?,
    val fullName: String,
    val isOrganizer: Boolean,
    val jobName: String,
    val personId: Int,
    val personalPhotoPath: String?,
    val qid: String?,
    val refuseReason: String?,
    val rejectionReason: RejectionReason,
    val status: String
)