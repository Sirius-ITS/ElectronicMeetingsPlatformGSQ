package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable

@Serializable
data class Attendee(
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
    val refuseReasonId: Int? = null,
    val rejectionReason: RejectionReason? = null,
    val status: String? = null
)

@Serializable
data class RejectionReason(
    val refuseReasonId: Int? = null,
    val refuseReason: String? = null,
    val refuseReasonEn: String? = null
)