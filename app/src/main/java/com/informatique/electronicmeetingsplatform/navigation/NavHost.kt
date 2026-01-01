package com.informatique.electronicmeetingsplatform.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.ui.screens.HomeScreen
import com.informatique.electronicmeetingsplatform.ui.screens.LoginScreen
import com.informatique.electronicmeetingsplatform.ui.viewModel.ThemeViewModel

@Composable
fun NavHost(themeViewModel: ThemeViewModel){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LoginRoute.route
    ) {

        composable(NavRoutes.LoginRoute.route) {
            LoginScreen(navController = navController)
        }

        composable(NavRoutes.HomeRoute.route) {
            HomeScreen(navController = navController)
        }
    }

}