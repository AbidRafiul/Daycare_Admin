package com.klmpk5.daycare_admin.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klmpk5.daycare_admin.ui.theme.screen.login.LoginScreen
import com.klmpk5.daycare_admin.ui.theme.screen.classroom.ClassroomScreen
import com.klmpk5.daycare_admin.ui.theme.screen.dashboard.DashboardScreen
import com.klmpk5.daycare_admin.ui.theme.screen.raport.RaportScreen
import com.klmpk5.daycare_admin.ui.theme.screen.chat.ChatScreen
import com.klmpk5.daycare_admin.ui.theme.screen.profile.ProfileScreen
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(navController = navController)
        }

        composable("classroom") {
            ClassroomScreen(
                adminChildViewModel = adminChildViewModel,
                attendanceViewModel = attendanceViewModel
            )
        }

        composable("raport") {
            RaportScreen()
        }

        composable("chat") {
            ChatScreen()
        }

        composable("profile") {
            ProfileScreen()
        }
    }
}