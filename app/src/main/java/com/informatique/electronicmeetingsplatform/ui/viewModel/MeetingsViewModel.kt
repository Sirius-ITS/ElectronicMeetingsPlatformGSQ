package com.informatique.electronicmeetingsplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.meeting.AllMeetingDetailUseCase
import com.informatique.electronicmeetingsplatform.business.meeting.AllMeetingsUseCase
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Data
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.Meeting
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.MeetingDetail
import com.informatique.electronicmeetingsplatform.di.security.EnvironmentConfig
import com.informatique.electronicmeetingsplatform.ui.components.popup.AlertPopupManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val allMeetingsUseCase: AllMeetingsUseCase,
    private val allMeetingDetailUseCase: AllMeetingDetailUseCase,
    private val alertPopupManager: AlertPopupManager
) : ViewModel() {

    // All meetings state
    private val _allMeetingState = MutableStateFlow<AllMeetingState>(AllMeetingState.Idle)
    val allMeetingState: StateFlow<AllMeetingState> = _allMeetingState.asStateFlow()

    // Meeting detail state
    private val _meetingDetailState = MutableStateFlow<MeetingDetailState>(MeetingDetailState.Idle)
    val meetingDetailState: StateFlow<MeetingDetailState> = _meetingDetailState.asStateFlow()


    // Add a flag to track if invitees have been loaded
    // private var inviteesLoaded = false

    private val _selectedMeeting = MutableStateFlow<Meeting?>(null)
    val selectedMeeting: StateFlow<Meeting?> = _selectedMeeting.asStateFlow()

    fun selectMeeting(meeting: Meeting) {
        _selectedMeeting.value = meeting
    }

    init {
        allMeetings()
    }

    fun getMediaUrl(): String {
        return EnvironmentConfig.currentEnvironment.mediaUrl
    }

    fun allMeetings() {
        viewModelScope.launch {
            _allMeetingState.value = AllMeetingState.Loading

            when (val result = allMeetingsUseCase()) {
                is BusinessState.Success -> {
                    _allMeetingState.value = AllMeetingState.Success(result.data.data)
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

    fun meetingDetail(meetingId: Int) {
        viewModelScope.launch {
            _meetingDetailState.value = MeetingDetailState.Loading

            when (val result = allMeetingDetailUseCase(AllMeetingDetailUseCase.Params(meetingId))) {
                is BusinessState.Success -> {
                    _meetingDetailState.value = MeetingDetailState.Success(result.data.data)
                }
                is BusinessState.Error -> {
                    _meetingDetailState.value = MeetingDetailState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _meetingDetailState.value = MeetingDetailState.Loading
                }
            }
        }
    }

//    fun clearInviteesCache() {
//        inviteesLoaded = false
//        _inviteeState.value = InviteeState.Idle
//    }


    override fun onCleared() {
        super.onCleared()

    //    clearInviteesCache()
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
    data class Success(val data: MeetingDetail): MeetingDetailState()
    data class Error(val message: String): MeetingDetailState()
}