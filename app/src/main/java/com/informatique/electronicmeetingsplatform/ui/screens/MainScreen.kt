package com.informatique.electronicmeetingsplatform.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // State للتحكم في ظهور صفحة AddRequest كـ Modal
    var showAddRequestModal by remember { mutableStateOf(false) }

    // State للتحكم في ظهور صفحة OfficialTaskRequest كـ Modal
    var showOfficialTaskRequestModal by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        // المحتوى الأساسي
        NestedNavHost(
            navController = navController,
            nestedNavController = nestedNavController,
            onNavigateToAddRequest = { showAddRequestModal = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp)
        )

        // Bottom Navigation Bar
        BottomNavigationBar(
            navController = nestedNavController,
            extraColors = extraColors,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Add Request Modal - بيظهر فوق كل حاجة
        AnimatedVisibility(
            visible = showAddRequestModal,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            AddRequestScreen(
                navController = nestedNavController,
                onDismiss = { showAddRequestModal = false },
                onNavigateToOfficialTaskRequest = {
                    showAddRequestModal = false
                    showOfficialTaskRequestModal = true
                }
            )
        }

        // Official Task Request Modal - بنفس الـ animation
        AnimatedVisibility(
            visible = showOfficialTaskRequestModal,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            OfficialTaskRequestScreen(
                navController = nestedNavController,
                onDismiss = { showOfficialTaskRequestModal = false }
            )
        }
    }
}

@Composable
fun NestedNavHost(
    navController: NavController,
    nestedNavController: NavHostController,
    onNavigateToAddRequest: () -> Unit,
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
            MyRequestScreen(
                navController = nestedNavController,
                onNavigateToAddRequest = onNavigateToAddRequest
            )
        }

        composable(NavRoutes.PreviousMeetingRoute.route) {
            PreviousMeetingScreen(navController = navController)
        }

        composable(NavRoutes.ProfileRoute.route) {
            ProfileScreen(navController = navController)
        }
    }
}