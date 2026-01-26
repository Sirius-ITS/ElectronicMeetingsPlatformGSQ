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
        val contentResolver = context.contentResolver
        val values = android.content.ContentValues().apply {
            put(android.provider.CalendarContract.Events.DTSTART, detail.startDateTimeMillis)
            put(android.provider.CalendarContract.Events.DTEND, detail.endDateTimeMillis)
            put(android.provider.CalendarContract.Events.TITLE, detail.topic)
            put(android.provider.CalendarContract.Events.DESCRIPTION, detail.notes)
            put(android.provider.CalendarContract.Events.EVENT_LOCATION, detail.location)
            put(android.provider.CalendarContract.Events.CALENDAR_ID, 1) // Use primary calendar
            put(android.provider.CalendarContract.Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().id)
        }
        contentResolver.insert(android.provider.CalendarContract.Events.CONTENT_URI, values)
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