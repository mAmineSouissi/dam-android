package tn.esprit.dam_android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Home",
        iconSelected = Icons.Filled.Home,
        iconUnselected = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Screen.Scans.route,
        label = "Scans",
        iconSelected = Icons.Filled.Search,
        iconUnselected = Icons.Outlined.Search
    ),
    BottomNavItem(
        route = Screen.Apps.route,
        label = "Apps",
        iconSelected = Icons.Filled.Apps,
        iconUnselected = Icons.Outlined.Apps
    ),
    BottomNavItem(
        route = Screen.Alerts.route,
        label = "Alerts",
        iconSelected = Icons.Filled.Notifications,
        iconUnselected = Icons.Outlined.Notifications
    ),
    BottomNavItem(
        route = Screen.Settings.route,
        label = "Settings",
        iconSelected = Icons.Filled.Settings,
        iconUnselected = Icons.Outlined.Settings
    )
)