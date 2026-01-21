package com.informatique.electronicmeetingsplatform.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.meeting.MeetingStatisticsUseCase
import com.informatique.electronicmeetingsplatform.di.security.EnvironmentConfig
import com.informatique.electronicmeetingsplatform.data.model.meeting.statistics.Data as StatisticData
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val meetingStatisticsUseCase: MeetingStatisticsUseCase
) : ViewModel() {

    // Type state
    private val _statisticState = MutableStateFlow<StatisticState>(StatisticState.Idle)
    val statisticState: StateFlow<StatisticState> = _statisticState.asStateFlow()

    init {
        meetingStatistics()
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