package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class Meeting(
    val acceptedCount: Int,
    val attachments: List<Attachment>,
    val attendees: List<Attendee>,
    val canApologyAfterAccept: Boolean? = null,
    val endDateTime: String,
    val externalAttendees: List<Attendee>,
    val id: Int,
    val isOrganizer: Boolean? = null,
    val isRepeated: Boolean,
    val isSpecialType: Boolean? = null,
    val location: String,
    val meetingTypeId: Int? = null,
    val meetingTypeName: String? = null,
    val myStatus: String? = null,
    val pendingCount: Int,
    val priorityId: Int,
    val priorityName: String,
    val refusedCount: Int,
    val repeatRule: String,
    val startDateTime: String,
    val topic: String
){
    val startDate: String
        get() = try {
            val localDateTime = java.time.LocalDateTime.parse(startDateTime)
            val formatter = java.time.format.DateTimeFormatter.ofPattern(
                "dd MMMM yyyy",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }

    val startTime: String
        get() = try {
            val localDateTime = java.time.LocalDateTime.parse(startDateTime)
            val formatter = java.time.format.DateTimeFormatter.ofPattern(
                "HH:mm",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }

    val endDate: String
        get() = try {
            val localDateTime = java.time.LocalDateTime.parse(endDateTime)
            val formatter = java.time.format.DateTimeFormatter.ofPattern(
                "dd MMMM yyyy",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }

    val endTime: String
        get() = try {
            val localDateTime = java.time.LocalDateTime.parse(endDateTime)
            val formatter = java.time.format.DateTimeFormatter.ofPattern(
                "HH:mm",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }
}