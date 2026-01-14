package com.informatique.electronicmeetingsplatform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.ui.screens.LoginScreen
import com.informatique.electronicmeetingsplatform.ui.screens.MainScreen
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
            MainScreen()
        }

    }
}

