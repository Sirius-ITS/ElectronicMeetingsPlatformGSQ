package com.informatique.electronicmeetingsplatform.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.meeting.AllMeetingsUseCase
import com.informatique.electronicmeetingsplatform.business.meeting.RespondMeetingUseCase
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Data
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Meeting
import com.informatique.electronicmeetingsplatform.di.security.EnvironmentConfig
import com.informatique.electronicmeetingsplatform.ui.components.popup.AlertPopupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for All Meetings screen
 * Manages authentication state and user interactions
 */
@HiltViewModel
class MeetingsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val allMeetingsUseCase: AllMeetingsUseCase,
    private val respondMeetingUseCase: RespondMeetingUseCase,
    private val alertPopupManager: AlertPopupManager
) : ViewModel() {

    // All meetings state
    private val _allMeetingState = MutableStateFlow<AllMeetingState>(AllMeetingState.Idle)
    val allMeetingState: StateFlow<AllMeetingState> = _allMeetingState.asStateFlow()

    // Meeting detail state
    private val _meetingDetailState = MutableStateFlow<MeetingDetailState>(MeetingDetailState.Idle)
    val meetingDetailState: StateFlow<MeetingDetailState> = _meetingDetailState.asStateFlow()

    // Respond meeting state
    private val _respondMeetingState = MutableStateFlow<RespondMeetingState>(RespondMeetingState.Idle)
    val respondMeetingState: StateFlow<RespondMeetingState> = _respondMeetingState.asStateFlow()

    private var meetingsLoaded = false



    fun getMediaUrl(): String {
        return EnvironmentConfig.currentEnvironment.mediaUrl
    }

    fun allMeetings() {
        if (meetingsLoaded) {
            return
        }

        viewModelScope.launch {
            _allMeetingState.value = AllMeetingState.Loading

            when (val result = allMeetingsUseCase()) {
                is BusinessState.Success -> {
                    _allMeetingState.value = AllMeetingState.Success(result.data.data)
                    meetingsLoaded = true
                }
                is BusinessState.Error -> {
                    _allMeetingState.value = AllMeetingState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _allMeetingState.value = AllMeetingState.Loading
                }
            }
        }
    }

    fun getMeetingById(meetingId: Int) {
        val currentState = _allMeetingState.value
        if (currentState is AllMeetingState.Success) {
            _meetingDetailState.value = MeetingDetailState.Loading
            val meeting = (currentState.data.invited + currentState.data.organized).find {
                it.id == meetingId
            }

            if (meeting == null) {
                _meetingDetailState.value = MeetingDetailState.Error("Meeting not found")
            }
            else {
                _meetingDetailState.value = MeetingDetailState.Success(meeting)
            }
        }
    }

    fun addEventToCalendar(detail: Meeting) {
        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver

                // Check if event already exists
                if (isEventAlreadyInCalendar(detail)) {
                    android.widget.Toast.makeText(
                        context,
                        "هذا الاجتماع مضاف بالفعل إلى التقويم",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                // Get the primary calendar ID
                val calendarId = getPrimaryCalendarId(contentResolver)

                if (calendarId == null) {
                    android.widget.Toast.makeText(
                        context,
                        "لم يتم العثور على تقويم. يرجى إضافة حساب Google أولاً",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                val values = android.content.ContentValues().apply {
                    put(android.provider.CalendarContract.Events.DTSTART, detail.startDateTimeMillis)
                    put(android.provider.CalendarContract.Events.DTEND, detail.endDateTimeMillis)
                    put(android.provider.CalendarContract.Events.TITLE, detail.topic)
                    // Store meeting metadata in description as JSON
                    val metadata = """
                        [MEETING_ID:${detail.id}]
                        [PRIORITY_ID:${detail.priorityId}]
                        [PRIORITY_NAME:${detail.priorityName}]
                        
                        ${detail.notes}
                    """.trimIndent()
                    put(android.provider.CalendarContract.Events.DESCRIPTION, metadata)
                    put(android.provider.CalendarContract.Events.EVENT_LOCATION, detail.location)
                    put(android.provider.CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(android.provider.CalendarContract.Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().id)
                }

                val uri = contentResolver.insert(android.provider.CalendarContract.Events.CONTENT_URI, values)

                if (uri != null) {
                    android.widget.Toast.makeText(
                        context,
                        "تم إضافة الاجتماع إلى التقويم بنجاح",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    android.widget.Toast.makeText(
                        context,
                        "فشل إضافة الاجتماع إلى التقويم",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    context,
                    "خطأ: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isEventAlreadyInCalendar(detail: Meeting): Boolean {
        try {
            val contentResolver = context.contentResolver

            // Search for events with the same title and start time
            val projection = arrayOf(
                android.provider.CalendarContract.Events._ID,
                android.provider.CalendarContract.Events.TITLE,
                android.provider.CalendarContract.Events.DTSTART
            )

            // Create a time range (5 minutes before and after to account for slight differences)
            val startTimeBuffer = 5 * 60 * 1000 // 5 minutes in milliseconds
            val selectionArgs = arrayOf(
                detail.topic,
                (detail.startDateTimeMillis - startTimeBuffer).toString(),
                (detail.startDateTimeMillis + startTimeBuffer).toString()
            )

            val selection = "${android.provider.CalendarContract.Events.TITLE} = ? AND " +
                    "${android.provider.CalendarContract.Events.DTSTART} >= ? AND " +
                    "${android.provider.CalendarContract.Events.DTSTART} <= ?"

            val cursor = contentResolver.query(
                android.provider.CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )

            cursor?.use {
                if (it.count > 0) {
                    return true // Event already exists
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MeetingsViewModel", "Error checking calendar event: ${e.message}")
        }

        return false // Event doesn't exist
    }

    private fun getPrimaryCalendarId(contentResolver: android.content.ContentResolver): Long? {
        val projection = arrayOf(
            android.provider.CalendarContract.Calendars._ID,
            android.provider.CalendarContract.Calendars.IS_PRIMARY
        )

        val cursor = contentResolver.query(
            android.provider.CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(android.provider.CalendarContract.Calendars._ID)
            val isPrimaryColumn = it.getColumnIndex(android.provider.CalendarContract.Calendars.IS_PRIMARY)

            while (it.moveToNext()) {
                val isPrimary = it.getInt(isPrimaryColumn)
                if (isPrimary == 1) {
                    return it.getLong(idColumn)
                }
            }
            // If no primary calendar found, return the first one
            if (it.moveToFirst()) {
                return it.getLong(idColumn)
            }
        }

        return null
    }

    fun getEventsFromDeviceCalendar(startDate: java.time.LocalDate, endDate: java.time.LocalDate): List<Meeting> {
        val events = mutableListOf<Meeting>()

        try {
            val startMillis = startDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = endDate.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            val projection = arrayOf(
                android.provider.CalendarContract.Events._ID,
                android.provider.CalendarContract.Events.TITLE,
                android.provider.CalendarContract.Events.DTSTART,
                android.provider.CalendarContract.Events.DTEND,
                android.provider.CalendarContract.Events.EVENT_LOCATION,
                android.provider.CalendarContract.Events.DESCRIPTION
            )

            val selection = "(${android.provider.CalendarContract.Events.DTSTART} >= ? AND ${android.provider.CalendarContract.Events.DTSTART} <= ?)"
            val selectionArgs = arrayOf(startMillis.toString(), endMillis.toString())

            val cursor = context.contentResolver.query(
                android.provider.CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${android.provider.CalendarContract.Events.DTSTART} ASC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(android.provider.CalendarContract.Events._ID)
                val titleIndex = it.getColumnIndex(android.provider.CalendarContract.Events.TITLE)
                val startIndex = it.getColumnIndex(android.provider.CalendarContract.Events.DTSTART)
                val endIndex = it.getColumnIndex(android.provider.CalendarContract.Events.DTEND)
                val locationIndex = it.getColumnIndex(android.provider.CalendarContract.Events.EVENT_LOCATION)
                val descriptionIndex = it.getColumnIndex(android.provider.CalendarContract.Events.DESCRIPTION)

                while (it.moveToNext()) {
                    val calendarEventId = it.getLong(idIndex)
                     val title = it.getString(titleIndex) ?: ""
                     val startMillisEvent = it.getLong(startIndex)
                     val endMillisEvent = it.getLong(endIndex)
                     val location = it.getString(locationIndex) ?: ""
                     val description = it.getString(descriptionIndex) ?: ""

                     // Extract metadata from description
                     // If the event contains our MEETING_ID metadata use it. Otherwise use the calendar event id
                     // encoded as a negative int to ensure uniqueness and avoid colliding with server ids.
                     val meetingId = extractMetadata(description, "MEETING_ID")?.toIntOrNull()
                        ?: (-calendarEventId).toInt()
                     val priorityId = extractMetadata(description, "PRIORITY_ID")?.toIntOrNull() ?: 1
                     val priorityName = extractMetadata(description, "PRIORITY_NAME") ?: "عادية"
                     val cleanDescription = cleanMetadataFromDescription(description)

                    // Convert to ISO format for Meeting object
                    val startDateTime = java.time.Instant.ofEpochMilli(startMillisEvent)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()

                    val endDateTime = java.time.Instant.ofEpochMilli(endMillisEvent)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()

                    events.add(
                        Meeting(
                            id = meetingId,
                            topic = title,
                            startDateTime = startDateTime.toString(),
                            endDateTime = endDateTime.toString(),
                            location = location,
                            notes = cleanDescription,
                            isOrganizer = false,
                            myStatus = "Accepted",
                            attendees = emptyList(),
                            externalAttendees = emptyList(),
                            attachments = emptyList(),
                            priorityId = priorityId,
                            priorityName = priorityName,
                            acceptedCount = 0,
                            pendingCount = 0,
                            refusedCount = 0,
                            canApologyAfterAccept = false,
                            isRepeated = false,
                            repeatRule = "",
                            isSpecialType = false,
                            meetingTypeId = null,
                            meetingTypeName = null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MeetingsViewModel", "Error reading calendar events: ${e.message}")
        }

        return events
    }

    fun getAttachmentType(fileName: String): String {
        return when {
            fileName.endsWith(".jpg", ignoreCase = true) ||
                    fileName.endsWith(".jpeg", ignoreCase = true) ||
                    fileName.endsWith(".png", ignoreCase = true) ||
                    fileName.endsWith(".gif", ignoreCase = true) -> "صورة"
            fileName.endsWith(".pdf", ignoreCase = true) -> "PDF"
            fileName.endsWith(".doc", ignoreCase = true) ||
                    fileName.endsWith(".docx", ignoreCase = true) -> "Word"
            fileName.endsWith(".xls", ignoreCase = true) ||
                    fileName.endsWith(".xlsx", ignoreCase = true) -> "Excel"
            fileName.endsWith(".ppt", ignoreCase = true) ||
                    fileName.endsWith(".pptx", ignoreCase = true) -> "PowerPoint"
            fileName.endsWith(".mp4", ignoreCase = true) ||
                    fileName.endsWith(".avi", ignoreCase = true) -> "فيديو"
            fileName.endsWith(".mp3", ignoreCase = true) ||
                    fileName.endsWith(".wav", ignoreCase = true) -> "صوت"
            else -> "ملف"
        }
    }

    fun respondMeeting(
        meetingId: Int,
        response: String,
        reasonId: Int?,
        otherReason: String
    ) {

        viewModelScope.launch {
            _respondMeetingState.value = RespondMeetingState.Loading

            when (val result = respondMeetingUseCase(RespondMeetingUseCase.Params(
                meetingId = meetingId,
                response = response,
                reasonId = reasonId,
                otherReason = otherReason
            ))) {
                is BusinessState.Success -> {
                    _respondMeetingState.value = RespondMeetingState.Success(result.data.data)
                }
                is BusinessState.Error -> {
                    _respondMeetingState.value = RespondMeetingState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _respondMeetingState.value = RespondMeetingState.Loading
                }
            }
        }
    }

    fun clearAllMeetingsCache() {
        meetingsLoaded = false
        _allMeetingState.value = AllMeetingState.Idle
    }

    private fun extractMetadata(description: String, key: String): String? {
        val pattern = "\\[$key:(.*?)]".toRegex()
        return pattern.find(description)?.groupValues?.getOrNull(1)
    }

    private fun cleanMetadataFromDescription(description: String): String {
        return description
            .replace("\\[MEETING_ID:.*?]".toRegex(), "")
            .replace("\\[PRIORITY_ID:.*?]".toRegex(), "")
            .replace("\\[PRIORITY_NAME:.*?]".toRegex(), "")
            .trim()
    }


    public override fun onCleared() {
        super.onCleared()
        clearAllMeetingsCache()
    }

}

sealed class AllMeetingState {
    object Idle: AllMeetingState()
    object Loading: AllMeetingState()
    data class Success(val data: Data): AllMeetingState()
    data class Error(val message: String): AllMeetingState()
}

sealed class MeetingDetailState {
    object Idle: MeetingDetailState()
    object Loading: MeetingDetailState()
    data class Success(val data: Meeting): MeetingDetailState()
    data class Error(val message: String): MeetingDetailState()
}

sealed class RespondMeetingState {
    object Idle: RespondMeetingState()
    object Loading: RespondMeetingState()
    data class Success(val data: Meeting): RespondMeetingState()
    data class Error(val message: String): RespondMeetingState()
}