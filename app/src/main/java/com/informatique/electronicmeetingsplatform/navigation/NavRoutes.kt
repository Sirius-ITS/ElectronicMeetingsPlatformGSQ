package com.informatique.electronicmeetingsplatform.navigation

sealed class NavRoutes(val route: String) {
    data object HomeRoute : NavRoutes("homepage")

}