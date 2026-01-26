package com.informatique.electronicmeetingsplatform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.informatique.electronicmeetingsplatform.ui.screens.CreateMeetingScreen
import com.informatique.electronicmeetingsplatform.ui.screens.LoginScreen
import com.informatique.electronicmeetingsplatform.ui.screens.MainScreen
import com.informatique.electronicmeetingsplatform.ui.screens.meetings.AllMeetingScreen
import com.informatique.electronicmeetingsplatform.ui.screens.meetings.MeetingDetailScreen
import com.informatique.electronicmeetingsplatform.ui.screens.meetings.MeetingsType
import com.informatique.electronicmeetingsplatform.ui.viewModel.MeetingsViewModel
import com.informatique.electronicmeetingsplatform.ui.viewModel.ThemeViewModel

/**
 * Main NavHost for top-level navigation
 * Handles navigation between Login and Main screens
 */
@Composable
fun NavHost(
    themeViewModel: ThemeViewModel,
    onNavControllerReady: ((NavHostController) -> Unit)? = null
) {

    val navController = rememberNavController()
    val meetingsViewModel = hiltViewModel<MeetingsViewModel>()

    // Notify MainActivity when navController is ready
    LaunchedEffect(navController) {
        onNavControllerReady?.invoke(navController)
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LoginRoute.route
    ) {

        composable(NavRoutes.LoginRoute.route) {
            LoginScreen(navController = navController)
        }

        composable(NavRoutes.MainRoute.route) {
            MainScreen(navController = navController)
        }

        composable(NavRoutes.CreateMeetingRoute.route) {
            CreateMeetingScreen(navController = navController)
        }

        composable(
            route = NavRoutes.AllMeetingRoute.route,
            arguments = listOf(
                navArgument("meetingType") {
                    type = NavType.IntType
                    defaultValue = MeetingsType.All.ordinal
                }
            )
        ) { backStackEntry ->
            val meetingType = backStackEntry.arguments?.getInt("meetingType") ?: MeetingsType.All.ordinal
            AllMeetingScreen(
                viewModel = meetingsViewModel,
                navController = navController,
                type = meetingType
            )
        }

        composable(
            route = NavRoutes.MeetingDetailRoute.route,
            arguments = listOf(
                navArgument("meetingId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            MeetingDetailScreen(
                viewModel = meetingsViewModel,
                navController = navController,
                meetingId = meetingId
            )
        }

    }
}

