package com.informatique.electronicmeetingsplatform.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.informatique.electronicmeetingsplatform.ui.viewModel.ThemeViewModel

@Composable
fun NavHost(themeViewModel: ThemeViewModel, navigationManager: NavigationManagerImpl){

    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navigationManager.navigationCommands.collect { command ->
            when (command) {
                is NavigationCommand.Navigate -> {
                    navController.navigate(command.route) {
                        command.popUpTo?.let { route ->
                            popUpTo(route) {
                                inclusive = command.inclusive
                            }
                        }
                        launchSingleTop = command.singleTop
                    }
                }

                NavigationCommand.NavigateBack -> {
                    navController.popBackStack()
                }

                NavigationCommand.NavigateUp -> {
                    navController.navigateUp()
                }

                is NavigationCommand.PopBackStackTo -> {
                    navController.popBackStack(
                        route = command.route,
                        inclusive = command.inclusive
                    )
                }

                is NavigationCommand.NavigateAndClearBackStack -> {
                    navController.navigate(command.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                is NavigationCommand.NavigateWithArgs -> {
                    navController.navigate("${command.route}/${Uri.encode(command.data)}")
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HomeRoute.route
    ) {

        composable(NavRoutes.HomeRoute.route) {
            // HomePageScreen(navController = navController)
        }
    }

}