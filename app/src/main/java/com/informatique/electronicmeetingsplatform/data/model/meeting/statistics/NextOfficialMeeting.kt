package com.informatique.electronicmeetingsplatform.data.model.meeting.statistics

import kotlinx.serialization.Serializable

@Serializable
data class NextOfficialMeeting(
    val acceptedCount: Int,
    val attachments: List<Attachment>,
    val attendees: List<Attendee>,
    val endDateTime: String,
    val externalAttendees: List<Attendee?>,
    val id: Int,
    val isOffical: Boolean,
    val isRepeated: Boolean,
    val pendingCount: Int,
    val priorityId: Int,
    val priorityName: String,
    val refusedCount: Int,
    val repeatRule: String,
    val startDateTime: String,
    val title: String
)