package com.informatique.electronicmeetingsplatform.data.model.meeting.allMeeting

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val invited: List<Meeting>,
    val nextOfficialMeeting: Meeting?,
    val organized: List<Meeting>
)