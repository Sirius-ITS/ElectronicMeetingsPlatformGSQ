package com.informatique.electronicmeetingsplatform.data.model.meeting.create

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Data(
    val agendaItems: List<String?>? = null,
    val attachments: List<String>,
    val attendees: List<AttendeeX>,
    val creatorPersonFullName: String? = null,
    val creatorPersonId: Int,
    val endDateTime: String,
    val id: Int,
//    val isOffical: Boolean,
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
){
    val startDateTimeMillis: Long
        get() = try {
            val localDateTime = LocalDateTime.parse(startDateTime)
            localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Exception) {
            0L
        }

    val endDateTimeMillis: Long
        get() = try {
            val localDateTime = LocalDateTime.parse(endDateTime)
            localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Exception) {
            0L
        }
}