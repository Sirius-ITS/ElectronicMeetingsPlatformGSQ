package com.informatique.electronicmeetingsplatform.navigation

sealed class NavRoutes(val route: String) {
    data object LoginRoute : NavRoutes("login")
    data object MainRoute : NavRoutes("main")
    data object HomeRoute : NavRoutes("home")
    data object CalenderRoute : NavRoutes("calender")
    data object MyRequestsRoute : NavRoutes("requests")
    data object PreviousMeetingRoute : NavRoutes("previous-meetings")
    data object ProfileRoute : NavRoutes("profile")
    data object CreateMeetingRoute : NavRoutes("create-meeting")

}
