package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Serializable
data class MeetingDetail(
    val id: Int,
    val topic: String,
    val location: String,
    val startDateTime: String,
    val endDateTime: String,
    val isRepeated: Boolean,
    val repeatRule: String,
    val notes: String,
    val meetingTypeId: Int? = null,
    val meetingTypeName: String? = null,
    val meetingTypeNameEn: String? = null,
    val isSpecialType: Boolean? = null,
    val priorityId: Int,
    val priorityName: String,
    val priorityNameEn: String,
    val priorityOrder: Int,
    val acceptedCount: Int,
    val pendingCount: Int,
    val refusedCount: Int,
    val totalAttendees: Int,
    val attendancePercent: Int,
    val attachments: List<String>,
    val attendees: List<Attendee>,
    val externalAttendees: List<Attendee>
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

    val startDate: String
        get() = try {
            val localDateTime = LocalDateTime.parse(startDateTime)
            val formatter = DateTimeFormatter.ofPattern(
                "dd MMMM yyyy",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }

    val detailedStartDate: String get() = try {
        val localDateTime = LocalDateTime.parse(startDateTime)
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
        formatter.format(localDateTime)
    } catch (_: Exception) {
        startDateTime
    }

    val startTime: String
        get() = try {
            val localDateTime = LocalDateTime.parse(startDateTime)
            val formatter = DateTimeFormatter.ofPattern(
                "HH:mm",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }

    val endDate: String
        get() = try {
            val localDateTime = LocalDateTime.parse(endDateTime)
            val formatter = DateTimeFormatter.ofPattern(
                "dd MMMM yyyy",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }

    val endTime: String
        get() = try {
            val localDateTime = LocalDateTime.parse(endDateTime)
            val formatter = DateTimeFormatter.ofPattern(
                "HH:mm",
                Locale.getDefault()
            )
            formatter.format(localDateTime)
        } catch (_: Exception) {
            startDateTime
        }
}