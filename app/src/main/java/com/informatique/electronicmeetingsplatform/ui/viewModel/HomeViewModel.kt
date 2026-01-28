package com.informatique.electronicmeetingsplatform.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.meeting.MeetingStatisticsUseCase
import com.informatique.electronicmeetingsplatform.data.datastorehelper.TokenManager
import com.informatique.electronicmeetingsplatform.di.security.EnvironmentConfig
import com.informatique.electronicmeetingsplatform.data.model.meeting.statistics.Data as StatisticData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen
 * Manages the state of the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val meetingStatisticsUseCase: MeetingStatisticsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Type state
    private val _statisticState = MutableStateFlow<StatisticState>(StatisticState.Idle)
    val statisticState: StateFlow<StatisticState> = _statisticState.asStateFlow()

    // User full name state
    private val _userFullName = MutableStateFlow<String?>(null)
    val userFullName: StateFlow<String?> = _userFullName.asStateFlow()

    // User photo path state
    private val _personalPhotoPath = MutableStateFlow<String?>(null)
    val personalPhotoPath: StateFlow<String?> = _personalPhotoPath.asStateFlow()

    init {
        meetingStatistics()
        loadUserFullName()
        loadUserPhotoPath()
    }

    private fun loadUserFullName() {
        viewModelScope.launch {
            _userFullName.value = TokenManager.getFullName(context)
        }
    }

    private fun loadUserPhotoPath() {
        viewModelScope.launch {
            _personalPhotoPath.value = TokenManager.getPersonalPhotoPath(context)
            Log.d("HomeViewModel", "Loaded personalPhotoPath: ${_personalPhotoPath.value}")
        }
    }

    fun buildMediaUrl(fileName: String): String {
        Log.e("MediaURL", EnvironmentConfig.currentEnvironment.mediaUrl.plus(fileName))
        return EnvironmentConfig.currentEnvironment.mediaUrl.plus(fileName)
    }

    fun meetingStatistics() {
        viewModelScope.launch {
            _statisticState.value = StatisticState.Loading

            when (val result = meetingStatisticsUseCase()) {
                is BusinessState.Success -> {
                    _statisticState.value = StatisticState.Success(result.data.data)
                }
                is BusinessState.Error -> {
                    _statisticState.value = StatisticState.Error(result.message)
                }
                is BusinessState.Loading -> {
                    _statisticState.value = StatisticState.Loading
                }
            }
        }
    }

}

sealed class StatisticState {
    object Idle: StatisticState()
    object Loading: StatisticState()
    data class Success(val data: StatisticData): StatisticState()
    data class Error(val message: String): StatisticState()
}