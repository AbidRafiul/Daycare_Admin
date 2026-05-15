package com.klmpk5.daycare_admin.ui.theme.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted

private data class BottomNavItem(
    val icon: String,
    val label: String,
    val route: String
)

private val navItems = listOf(
    BottomNavItem("🏠", "Home",      "dashboard"),
    BottomNavItem("📄", "Raport",    "raport"),
    BottomNavItem("📅", "Classroom", "classroom"),
    BottomNavItem("💬", "Chat",      "chat"),
    BottomNavItem("👤", "Profile",   "profile")
)

@Composable
fun DashboardBottomNavigation(navController: NavController) {
    // Otomatis highlight tab sesuai route yang sedang aktif
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
    ) {
        navItems.forEach { item ->
            val selected = when {
                currentRoute?.startsWith("raport_history") == true -> item.route == "raport"
                currentRoute?.startsWith("profile/") == true -> item.route == "profile"
                else -> currentRoute == item.route
            }

            DashboardNavItem(
                icon = item.icon,
                label = item.label,
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("dashboard") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun RowScope.DashboardNavItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Text(text = icon, fontSize = 21.sp)
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = DaycarePrimary,
            selectedTextColor = DaycarePrimary,
            indicatorColor = DaycarePrimaryLight,
            unselectedIconColor = DaycareTextMuted,
            unselectedTextColor = DaycareTextMuted
        )
    )
}
