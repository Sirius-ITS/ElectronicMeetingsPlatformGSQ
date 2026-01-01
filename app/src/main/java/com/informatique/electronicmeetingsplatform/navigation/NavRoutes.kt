package com.informatique.electronicmeetingsplatform.navigation

sealed class NavRoutes(val route: String) {
    data object LoginRoute : NavRoutes("login")
    data object HomeRoute : NavRoutes("home")

}