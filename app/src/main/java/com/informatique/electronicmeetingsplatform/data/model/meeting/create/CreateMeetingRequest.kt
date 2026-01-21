package com.informatique.electronicmeetingsplatform.data.model.meeting.create

import kotlinx.serialization.Serializable

@Serializable
data class CreateMeetingRequest(
    val attachmentPaths: List<String>? = null,
    val attendees: List<Attendee>,
    val externalAttendees: List<Attendee>? = null,
    val dateFrom: String,
    val dateTo: String,
    val isPersonal: Boolean = false,
    val isRepeated: Boolean? = null,
    val location: String,
    val meetingPriorityId: Int,
    val meetingTypeId: Int,
    val notes: String? = null,
    val repeatRule: String? = null,
    val timeFrom: String,
    val timeTo: String,
    val topic: String
)