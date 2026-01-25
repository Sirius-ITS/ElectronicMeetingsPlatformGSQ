package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.Serializable

@Serializable
data class RejectionReason(
    val refuseReason: String? = null,
    val refuseReasonEn: String? = null,
    val refuseReasonId: Int? = null
)