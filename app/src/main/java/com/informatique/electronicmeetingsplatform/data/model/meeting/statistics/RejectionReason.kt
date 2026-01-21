package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.Serializable

@Serializable
data class RejectionReason(
    val refuseReason: String?,
    val refuseReasonEn: String?,
    val refuseReasonId: Int?
)