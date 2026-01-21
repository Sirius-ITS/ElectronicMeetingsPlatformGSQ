package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.navigation.NavRoutes
import com.informatique.electronicmeetingsplatform.ui.components.BottomNavigationBar
import com.informatique.electronicmeetingsplatform.ui.screens.profile.ProfileScreen
import com.informatique.electronicmeetingsplatform.ui.theme.LocalExtraColors

@Composable
fun MainScreen(navController: NavHostController) {

    val extraColors = LocalExtraColors.current

    val nestedNavController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NestedNavHost(
            navController = navController,
            nestedNavController = nestedNavController,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp) // Add padding to prevent content from being hidden behind the floating bar
        )

        // Floating bottom navigation bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(
                navController = nestedNavController,
                extraColors = extraColors
            )
        }
    }
}

@Composable
fun NestedNavHost(
    navController: NavController,
    nestedNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = nestedNavController,
        startDestination = NavRoutes.HomeRoute.route,
        modifier = modifier
    ) {
        composable(NavRoutes.HomeRoute.route) {
            HomeScreen(navController = navController)
        }

        composable(NavRoutes.CalenderRoute.route) {
            CalendarScreen(navController = navController)
        }

        composable(NavRoutes.MyRequestsRoute.route) {
            MyRequestScreen(navController = navController)
        }

        composable(NavRoutes.PreviousMeetingRoute.route) {
            PreviousMeetingScreen(navController = navController)
        }

        composable(NavRoutes.ProfileRoute.route) {
            ProfileScreen(navController = navController)
        }
    }
}