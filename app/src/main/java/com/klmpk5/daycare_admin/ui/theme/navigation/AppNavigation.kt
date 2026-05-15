package com.klmpk5.daycare_admin.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klmpk5.daycare_admin.ui.theme.screen.login.LoginScreen
import com.klmpk5.daycare_admin.ui.theme.screen.login.classroom.ClassroomScreen
import com.klmpk5.daycare_admin.ui.theme.screen.login.dashboard.DashboardScreen
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel,
    weeklyPlanViewModel: AdminWeeklyPlanViewModel
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
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen()
        }

        composable("classroom") {
            ClassroomScreen(
                adminChildViewModel = adminChildViewModel,
                attendanceViewModel = attendanceViewModel,
                weeklyPlanViewModel = weeklyPlanViewModel
            )
        }
    }
}