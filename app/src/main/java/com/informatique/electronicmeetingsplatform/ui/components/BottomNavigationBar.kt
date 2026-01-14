package com.informatique.electronicmeetingsplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.informatique.electronicmeetingsplatform.navigation.NavRoutes
import com.informatique.electronicmeetingsplatform.ui.theme.AppFontFamily
import com.informatique.electronicmeetingsplatform.ui.theme.ExtraColors

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    extraColors: ExtraColors
) {
    val items = listOf(
        BottomNavItem(
            route = NavRoutes.HomeRoute.route,
            label = "الرئيسية",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            route = NavRoutes.CalenderRoute.route,
            label = "التقويم",
            icon = Icons.Default.DateRange
        ),
        BottomNavItem(
            route = NavRoutes.MyRequestsRoute.route,
            label = "الطلبات",
            icon = Icons.Default.FileCopy
        ),
        BottomNavItem(
            route = NavRoutes.PreviousMeetingRoute.route,
            label = "الإجتماعات السابقة",
            icon = Icons.Default.Timelapse
        ),
        BottomNavItem(
            route = NavRoutes.ProfileRoute.route,
            label = "الملف الشخصي",
            icon = Icons.Default.Person
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BottomNavItemView(
                    extraColors = extraColors,
                    label = item.label,
                    icon = item.icon,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(NavRoutes.HomeRoute.route) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavItemView(
    extraColors: ExtraColors,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isSelected) extraColors.maroonColor else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontFamily = AppFontFamily,
            fontSize = 10.sp,
            color = if (isSelected) extraColors.maroonColor else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

