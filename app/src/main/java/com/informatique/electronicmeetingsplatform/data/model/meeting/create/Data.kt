package com.informatique.electronicmeetingsplatform.data.model.meeting.create

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val agendaItems: List<String?>,
    val attachments: List<String>,
    val attendees: List<AttendeeX>,
    val creatorPersonFullName: String,
    val creatorPersonId: Int,
    val endDateTime: String,
    val id: Int,
    val isOffical: Boolean,
    val isPersonal: Boolean,
    val isRepeated: Boolean,
    val isSpecialType: Boolean,
    val location: String,
    val meetingPriorityId: Int,
    val meetingPriorityName: String,
    val meetingTypeId: Int,
    val meetingTypeName: String,
    val notes: String,
    val repeatRule: String,
    val startDateTime: String,
    val topic: String
)