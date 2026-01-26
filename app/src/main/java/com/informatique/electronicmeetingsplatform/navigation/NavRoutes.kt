package com.informatique.electronicmeetingsplatform.navigation

import android.net.Uri
import com.informatique.electronicmeetingsplatform.ui.screens.meetings.MeetingsType

sealed class NavRoutes(val route: String) {
    data object LoginRoute : NavRoutes("login")
    data object MainRoute : NavRoutes("main")
    data object HomeRoute : NavRoutes("home")
    data object CalenderRoute : NavRoutes("calender")
    data object MyRequestsRoute : NavRoutes("requests")
    data object PreviousMeetingRoute : NavRoutes("previous-meetings")
    data object ProfileRoute : NavRoutes("profile")
    data object CreateMeetingRoute : NavRoutes("create-meeting")
    data object AllMeetingRoute : NavRoutes("all-meetings/{meetingType}"){
        fun createRoute(meetingType: MeetingsType) = "all-meetings/${Uri.encode(meetingType.ordinal.toString())}"
    }
    data object MeetingDetailRoute : NavRoutes("meeting-detail/{meetingId}"){
        fun createRoute(meetingId: String) = "meeting-detail/${Uri.encode(meetingId)}"
    }

}
