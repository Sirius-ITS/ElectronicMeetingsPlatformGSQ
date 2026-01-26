package com.informatique.electronicmeetingsplatform.ui.viewModel

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.meeting.CreateMeetingUseCase
import com.informatique.electronicmeetingsplatform.business.meeting.DeleteAttachmentUseCase
import com.informatique.electronicmeetingsplatform.business.meeting.MeetingAttachmentsUseCase
import com.informatique.electronicmeetingsplatform.business.meeting.MeetingInviteesUseCase
import com.informatique.electronicmeetingsplatform.business.meeting.MeetingPrioritiesUseCase
import com.informatique.electronicmeetingsplatform.business.meeting.MeetingTypesUseCase
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Meeting
import com.informatique.electronicmeetingsplatform.data.model.meeting.attachments.AttachmentResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.Attendee
import com.informatique.electronicmeetingsplatform.di.security.EnvironmentConfig
import com.informatique.electronicmeetingsplatform.ui.components.popup.AlertPopupManager
import com.informatique.electronicmeetingsplatform.data.model.meeting.type.Data as TypeData
import com.informatique.electronicmeetingsplatform.data.model.meeting.priorities.Data as PriorityData
import com.informatique.electronicmeetingsplatform.data.model.meeting.create.Data as CreateMeetingData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for Login screen
 * Manages authentication state and user interactions
 */
@HiltViewModel
class CreateMeetingViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val meetingTypesUseCase: MeetingTypesUseCase,
    private val meetingPrioritiesUseCase: MeetingPrioritiesUseCase,
    private val meetingInviteesUseCase: MeetingInviteesUseCase,
    private val meetingAttachmentsUseCase: MeetingAttachmentsUseCase,
    private val deleteAttachmentUseCase: DeleteAttachmentUseCase,
    private val createMeetingUseCase: CreateMeetingUseCase,
    private val alertPopupManager: AlertPopupManager
) : ViewModel() {

    // Type state
    private val _typeState = MutableStateFlow<TypeState>(TypeState.Idle)
    val typeState: StateFlow<TypeState> = _typeState.asStateFlow()

    // Priority state
    private val _priorityState = MutableStateFlow<PriorityState>(PriorityState.Idle)
    val priorityState: StateFlow<PriorityState> = _priorityState.asStateFlow()

    // Invitees state
    private val _inviteeState = MutableStateFlow<InviteeState>(InviteeState.Idle)
    val inviteeState: StateFlow<InviteeState> = _inviteeState.asStateFlow()

    // Attachment state
    private val _attachmentState = MutableStateFlow<AttachmentState>(AttachmentState.Idle)
    val attachmentState: StateFlow<AttachmentState> = _attachmentState.asStateFlow()

    // Delete attachment state
    private val _deleteAttachmentState = MutableStateFlow<DeleteAttachmentState>(DeleteAttachmentState.Idle)
    val deleteAttachmentState: StateFlow<DeleteAttachmentState> = _deleteAttachmentState.asStateFlow()

    // Create meeting state
    private val _createMeetingState = MutableStateFlow<CreateMeetingState>(CreateMeetingState.Idle)
    val createMeetingState: StateFlow<CreateMeetingState> = _createMeetingState.asStateFlow()

    // Form fields
    private val _topic = MutableStateFlow("")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _dateFrom = MutableStateFlow("")

    private val _dateTo = MutableStateFlow("")

    private val _timeFrom = MutableStateFlow("")

    private val _timeTo = MutableStateFlow("")

    val meetingDuration: StateFlow<Triple<Int, Int, Int>> = combine(_dateFrom, _timeFrom, _dateTo, _timeTo) { startDate, startTime, endDate, endTime ->
        if (startDate.isBlank() || startTime.isBlank() || endDate.isBlank() || endTime.isBlank()) {
            return@combine Triple(0, 0, 0)
        }
        try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            val startLocalDate = java.time.LocalDate.parse(startDate, dateFormatter)
            val startLocalTime = java.time.LocalTime.parse(startTime, timeFormatter)
            val endLocalDate = java.time.LocalDate.parse(endDate, dateFormatter)
            val endLocalTime = java.time.LocalTime.parse(endTime, timeFormatter)

            val start = LocalDateTime.of(startLocalDate, startLocalTime)
            val end = LocalDateTime.of(endLocalDate, endLocalTime)

            val duration = if (end.isAfter(start)) Duration.between(start, end) else Duration.ZERO
            Triple(duration.toDays().toInt(), duration.toHoursPart(), duration.toMinutesPart())
        } catch (e: Exception) {
            Triple(0, 0, 0)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0, 0))


    private val _meetingTypeId = MutableStateFlow<TypeData?>(null)
    val meetingTypeId: StateFlow<TypeData?> = _meetingTypeId.asStateFlow()

    private val _meetingPriorityId = MutableStateFlow<PriorityData?>(null)
    val meetingPriorityId: StateFlow<PriorityData?> = _meetingPriorityId.asStateFlow()

    private val _attendees = MutableStateFlow<List<Attendee>>(emptyList())
    val attendees: StateFlow<List<Attendee>> = _attendees.asStateFlow()

    // Save button enabled state
    private val _isSaveEnabled = MutableStateFlow(false)
    val isSaveEnabled: StateFlow<Boolean> = _isSaveEnabled.asStateFlow()

    // Add a flag to track if invitees have been loaded
    private var inviteesLoaded = false


    init {
        meetingTypes()
        meetingPriorities()
        observeFormValidation()
    }

    fun getMediaUrl(): String {
        return EnvironmentConfig.currentEnvironment.mediaUrl
    }

    private fun observeFormValidation() {
        viewModelScope.launch {
            combine(
                _topic, _dateFrom, _dateTo, _timeFrom, _timeTo,
                _meetingTypeId, _meetingPriorityId, _attendees
            ) { values ->
                val topic = values[0] as String
                val dateFrom = values[1] as String
                val dateTo = values[2] as String
                val timeFrom = values[3] as String
                val timeTo = values[4] as String
                val typeId = (values[5] as TypeData?)?.id
                val priorityId = (values[6] as PriorityData?)?.id
                val attendees = values[7] as List<*>

                topic.isNotBlank() &&
                        dateFrom.isNotBlank() &&
                        dateTo.isNotBlank() &&
                        timeFrom.isNotBlank() &&
                        timeTo.isNotBlank() &&
                        typeId != null &&
                        priorityId != null &&
                        attendees.isNotEmpty()
            }.collect { isValid ->
                _isSaveEnabled.value = isValid
            }
        }
    }

    fun meetingTypes() {
        viewModelScope.launch {
            _typeState.value = TypeState.Loading

            when (val result = meetingTypesUseCase()) {
                is BusinessState.Success -> {
                    _typeState.value = TypeState.Success(result.data.data)
                }
                is BusinessState.Error -> {
                    _typeState.value = TypeState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _typeState.value = TypeState.Loading
                }
            }
        }
    }

    fun meetingPriorities() {
        viewModelScope.launch {
            _priorityState.value = PriorityState.Loading

            when (val result = meetingPrioritiesUseCase()) {
                is BusinessState.Success -> {
                    _priorityState.value = PriorityState.Success(result.data.data)
                }
                is BusinessState.Error -> {
                    _priorityState.value = PriorityState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _priorityState.value = PriorityState.Loading
                }
            }
        }
    }

    fun meetingInvitees(forceRefresh: Boolean = false) {
        // Skip if already loaded and not forcing refresh
        if (inviteesLoaded && !forceRefresh) {
            return
        }

        viewModelScope.launch {
            _inviteeState.value = InviteeState.Loading

            when (val result = meetingInviteesUseCase()) {
                is BusinessState.Success -> {
                    _inviteeState.value = InviteeState.Success(result.data.data.data)
                    inviteesLoaded = true
                }
                is BusinessState.Error -> {
                    _inviteeState.value = InviteeState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _inviteeState.value = InviteeState.Loading
                }
            }
        }
    }

    fun clearInviteesCache() {
        inviteesLoaded = false
        _inviteeState.value = InviteeState.Idle
    }

    fun meetingAttachments(fileName: String, file: Uri) {

        viewModelScope.launch {
            _attachmentState.value = AttachmentState.Loading

            when (val result = meetingAttachmentsUseCase(
                MeetingAttachmentsUseCase.Params(fileName = fileName, file = file))) {
                is BusinessState.Success -> {
                    _attachmentState.value = AttachmentState.Success(result.data)
                }
                is BusinessState.Error -> {
                    _attachmentState.value = AttachmentState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _attachmentState.value = AttachmentState.Loading
                }
            }
        }
    }

    fun onDeleteAttachment(fileName: String) {
        // Show delete confirmation dialog (BLOCKING - requires user action)
        alertPopupManager.showDeleteConfirmation(
            message = "هل تريد حذف هذا الملف ؟ لا يمكن التراجع عن هذا الإجراء.",
            title = "تأكيد الحذف",
            itemName = fileName,
            confirmText = "حذف",
            cancelText = "إلغاء",
            onConfirm = {
                // User confirmed, perform delete
                deleteAttachment(fileName)
            }
        )
    }

    private fun deleteAttachment(
        fileName: String
    ) {

        viewModelScope.launch {
            _deleteAttachmentState.value = DeleteAttachmentState.Loading

            when (val result = deleteAttachmentUseCase(DeleteAttachmentUseCase.Params(fileName))) {
                is BusinessState.Success -> {
                    _deleteAttachmentState.value = DeleteAttachmentState.Success(
                        isDeleted = result.data.success, fileName = fileName)
                }
                is BusinessState.Error -> {
                    _deleteAttachmentState.value = DeleteAttachmentState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _deleteAttachmentState.value = DeleteAttachmentState.Loading
                }
            }
        }
    }

    fun createMeeting(
        attachmentPaths: List<String>?,
        externalAttendees: List<Attendee>?,
        isRepeated: Boolean = false,
        repeatRule: String?,
        notes: String?
    ) {

        if (!_isSaveEnabled.value) return

        viewModelScope.launch {
            _createMeetingState.value = CreateMeetingState.Loading

            when (val result = createMeetingUseCase(CreateMeetingUseCase.Params(
                attachmentPaths = attachmentPaths,
                externalAttendees = externalAttendees,
                attendees = _attendees.value,
                dateFrom = _dateFrom.value,
                timeFrom = _timeFrom.value,
                dateTo = _dateTo.value,
                timeTo = _timeTo.value,
                isPersonal = false,
                isRepeated = isRepeated,
                location = _location.value,
                meetingPriorityId = _meetingPriorityId.value?.id!!,
                meetingTypeId = _meetingTypeId.value?.id!!,
                notes = notes,
                repeatRule = repeatRule,
                topic = _topic.value
            ))) {
                is BusinessState.Success -> {
                    _createMeetingState.value = CreateMeetingState.Success(result.data.data)
                }
                is BusinessState.Error -> {
                    _createMeetingState.value = CreateMeetingState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _createMeetingState.value = CreateMeetingState.Loading
                }
            }
        }
    }

    fun addEventToCalendar(detail: CreateMeetingData, onClick: () -> Unit) {
        try {
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
            viewModelScope.launch {
                alertPopupManager.showSuccess(
                    message = "تم حفظ بيانات الاجتماع واضافته للتقويم بنجاح",
                    actionLabel = "موافق",
                    onAction = onClick
                )
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun updateTopic(value: String) { _topic.value = value }
    fun updateLocation(value: String) { _location.value = value }
    fun updateDateFrom(value: String) { _dateFrom.value = value }
    fun updateDateTo(value: String) { _dateTo.value = value }
    fun updateTimeFrom(value: String) { _timeFrom.value = value }
    fun updateTimeTo(value: String) { _timeTo.value = value }
    fun updateMeetingTypeId(value: TypeData) { _meetingTypeId.value = value }
    fun updateMeetingPriorityId(value: PriorityData) { _meetingPriorityId.value = value }
    fun updateAttendees(value: List<Attendee>) { _attendees.value = value }

    override fun onCleared() {
        super.onCleared()

        clearInviteesCache()
    }

}

sealed class TypeState {
    object Idle: TypeState()
    object Loading: TypeState()
    data class Success(val data: List<TypeData>): TypeState()
    data class Error(val message: String): TypeState()
}

sealed class PriorityState {
    object Idle: PriorityState()
    object Loading: PriorityState()
    data class Success(val data: List<PriorityData>): PriorityState()
    data class Error(val message: String): PriorityState()
}

sealed class InviteeState {
    object Idle: InviteeState()
    object Loading: InviteeState()
    data class Success(val data: List<Attendee>): InviteeState()
    data class Error(val message: String): InviteeState()
}

sealed class AttachmentState {
    object Idle: AttachmentState()
    object Loading: AttachmentState()
    data class Success(val data: AttachmentResponse): AttachmentState()
    data class Error(val message: String): AttachmentState()
}

sealed class DeleteAttachmentState {
    object Idle: DeleteAttachmentState()
    object Loading: DeleteAttachmentState()
    data class Success(val isDeleted: Boolean, val fileName: String): DeleteAttachmentState()
    data class Error(val message: String): DeleteAttachmentState()
}

sealed class CreateMeetingState {
    object Idle: CreateMeetingState()
    object Loading: CreateMeetingState()
    data class Success(val data: CreateMeetingData): CreateMeetingState()
    data class Error(val message: String): CreateMeetingState()
}
