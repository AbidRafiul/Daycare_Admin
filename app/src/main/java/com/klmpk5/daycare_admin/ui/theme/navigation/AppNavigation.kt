package com.klmpk5.daycare_admin.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klmpk5.daycare_admin.ui.screen.raport.HistoryRaportScreen
import com.klmpk5.daycare_admin.ui.screen.raport.RaportScreen
import com.klmpk5.daycare_admin.ui.theme.screen.login.LoginScreen
import com.klmpk5.daycare_admin.ui.theme.screen.classroom.ClassroomScreen
import com.klmpk5.daycare_admin.ui.theme.screen.dashboard.DashboardScreen
import com.klmpk5.daycare_admin.ui.theme.screen.chat.ChatScreen
import com.klmpk5.daycare_admin.ui.theme.screen.profile.ProfileScreen
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminScoreViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel,
    weeklyPlanViewModel: AdminWeeklyPlanViewModel,
    scoreViewModel: AdminScoreViewModel
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
                attendanceViewModel = attendanceViewModel,
                weeklyPlanViewModel = weeklyPlanViewModel
            )
        }

        composable("raport") {
            RaportScreen(
                adminChildViewModel = adminChildViewModel,
                onOpenHistory = { childId ->
                    navController.navigate("raport_history/$childId")
                }
            )
        }

        composable("raport_history/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId").orEmpty()

            HistoryRaportScreen(
                childId = childId,
                adminChildViewModel = adminChildViewModel,
                scoreViewModel = scoreViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("chat") {
            ChatScreen()
        }

        composable("profile") {
            ProfileScreen()
        }
    }
}