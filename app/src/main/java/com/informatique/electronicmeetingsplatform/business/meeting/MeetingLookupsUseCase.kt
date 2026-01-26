package com.informatique.electronicmeetingsplatform.business.meeting

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCaseWithoutParams
import com.informatique.electronicmeetingsplatform.data.model.meeting.invitees.InviteesResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.priorities.MeetingPrioritiesResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.statistics.MeetingStatisticsResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.type.MeetingTypeResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepository
import javax.inject.Inject

class MeetingStatisticsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCaseWithoutParams<MeetingStatisticsResponse>() {

    override suspend fun invoke(): BusinessState<MeetingStatisticsResponse> {

        var finalResponse: ApiResponse<MeetingStatisticsResponse> = ApiResponse.Loading

        meetingRepository.meetingStatistics().collect { response ->
            // Skip Loading state, wait for actual response
            if (response !is ApiResponse.Loading) {
                finalResponse = response
            }
        }

        // Extract the final response to a local val for smart cast
        return when (val result = finalResponse) {
            is ApiResponse.Success -> {
                BusinessState.Success(result.data)
            }
            is ApiResponse.Error -> {
                BusinessState.Error(result.message)
            }
            is ApiResponse.NetworkError -> {
                BusinessState.Error("Network error. Please check your connection.")
            }
            is ApiResponse.Loading -> {
                // This shouldn't happen, but handle it just in case
                BusinessState.Loading
            }
        }
    }

}

class MeetingTypesUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCaseWithoutParams<MeetingTypeResponse>() {

    override suspend fun invoke(): BusinessState<MeetingTypeResponse> {

        var finalResponse: ApiResponse<MeetingTypeResponse> = ApiResponse.Loading

        meetingRepository.meetingTypes().collect { response ->
            // Skip Loading state, wait for actual response
            if (response !is ApiResponse.Loading) {
                finalResponse = response
            }
        }

        // Extract the final response to a local val for smart cast
        return when (val result = finalResponse) {
            is ApiResponse.Success -> {
                BusinessState.Success(result.data)
            }
            is ApiResponse.Error -> {
                BusinessState.Error(result.message)
            }
            is ApiResponse.NetworkError -> {
                BusinessState.Error("Network error. Please check your connection.")
            }
            is ApiResponse.Loading -> {
                // This shouldn't happen, but handle it just in case
                BusinessState.Loading
            }
        }
    }

}

class MeetingPrioritiesUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCaseWithoutParams<MeetingPrioritiesResponse>() {

    override suspend fun invoke(): BusinessState<MeetingPrioritiesResponse> {

        var finalResponse: ApiResponse<MeetingPrioritiesResponse> = ApiResponse.Loading

        meetingRepository.meetingPriorities().collect { response ->
            // Skip Loading state, wait for actual response
            if (response !is ApiResponse.Loading) {
                finalResponse = response
            }
        }

        // Extract the final response to a local val for smart cast
        return when (val result = finalResponse) {
            is ApiResponse.Success -> {
                BusinessState.Success(result.data)
            }
            is ApiResponse.Error -> {
                BusinessState.Error(result.message)
            }
            is ApiResponse.NetworkError -> {
                BusinessState.Error("Network error. Please check your connection.")
            }
            is ApiResponse.Loading -> {
                // This shouldn't happen, but handle it just in case
                BusinessState.Loading
            }
        }
    }

}

class MeetingInviteesUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCaseWithoutParams<InviteesResponse>() {

    override suspend fun invoke(): BusinessState<InviteesResponse> {

        var finalResponse: ApiResponse<InviteesResponse> = ApiResponse.Loading

        meetingRepository.meetingInvitees().collect { response ->
            // Skip Loading state, wait for actual response
            if (response !is ApiResponse.Loading) {
                finalResponse = response
            }
        }

        // Extract the final response to a local val for smart cast
        return when (val result = finalResponse) {
            is ApiResponse.Success -> {
                BusinessState.Success(result.data)
            }
            is ApiResponse.Error -> {
                BusinessState.Error(result.message)
            }
            is ApiResponse.NetworkError -> {
                BusinessState.Error("Network error. Please check your connection.")
            }
            is ApiResponse.Loading -> {
                // This shouldn't happen, but handle it just in case
                BusinessState.Loading
            }
        }
    }

}


