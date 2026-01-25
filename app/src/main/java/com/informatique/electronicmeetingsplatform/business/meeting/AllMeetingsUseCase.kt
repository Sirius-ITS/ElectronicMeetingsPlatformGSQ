package com.informatique.electronicmeetingsplatform.business.meeting

import com.informatique.electronicmeetingsplatform.business.BusinessState
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCase
import com.informatique.electronicmeetingsplatform.business.base.BaseUseCaseWithoutParams
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingDetailResponse
import com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting.AllMeetingResponse
import com.informatique.electronicmeetingsplatform.data.remote.common.ApiResponse
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepository
import javax.inject.Inject

class AllMeetingsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCaseWithoutParams<AllMeetingResponse>() {

    override suspend fun invoke(): BusinessState<AllMeetingResponse> {

        var finalResponse: ApiResponse<AllMeetingResponse> = ApiResponse.Loading

        meetingRepository.allMeetings().collect { response ->
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

class AllMeetingDetailUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseUseCase<AllMeetingDetailUseCase.Params, AllMeetingDetailResponse>() {

    data class Params(
        val meetingId: Int
    )

    override suspend fun invoke(parameters: Params): BusinessState<AllMeetingDetailResponse> {

        var finalResponse: ApiResponse<AllMeetingDetailResponse> = ApiResponse.Loading

        meetingRepository.allMeetingDetail(parameters.meetingId).collect { response ->
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