package com.informatique.electronicmeetingsplatform.data.model.meeting.invitees

import com.informatique.electronicmeetingsplatform.data.model.meeting.create.Attendee
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val data: List<Attendee>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int
)