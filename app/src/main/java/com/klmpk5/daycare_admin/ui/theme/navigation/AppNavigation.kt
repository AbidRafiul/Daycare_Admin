package com.klmpk5.daycare_admin.ui.theme.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.repository.ChatRepository
import com.klmpk5.daycare_admin.viewmodel.ChatViewModel
import com.klmpk5.daycare_admin.viewmodel.ChatViewModelFactory

import com.klmpk5.daycare_admin.ui.screen.raport.HistoryRaportScreen
import com.klmpk5.daycare_admin.ui.screen.raport.RaportScreen
import com.klmpk5.daycare_admin.ui.screen.profile.AdminManagementPage
import com.klmpk5.daycare_admin.ui.screen.profile.ChangePasswordScreen
import com.klmpk5.daycare_admin.ui.screen.profile.EditProfileScreen
import com.klmpk5.daycare_admin.ui.screen.profile.ProfileScreen
import com.klmpk5.daycare_admin.ui.theme.screen.login.LoginScreen
import com.klmpk5.daycare_admin.ui.theme.screen.classroom.ClassroomScreen
import com.klmpk5.daycare_admin.ui.theme.screen.dashboard.DashboardBottomNavigation
import com.klmpk5.daycare_admin.ui.theme.screen.dashboard.DashboardScreen
import com.klmpk5.daycare_admin.ui.theme.screen.chat.ChatScreen
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminManagementViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminScoreViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel
import com.klmpk5.daycare_admin.viewmodel.ProfileViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel,
    weeklyPlanViewModel: AdminWeeklyPlanViewModel,
    scoreViewModel: AdminScoreViewModel,
    profileViewModel: ProfileViewModel,
    adminManagementViewModel: AdminManagementViewModel
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute != null && currentRoute != "login"
    val firebaseService = remember { FirebaseService() }
    val chatRepository = remember { ChatRepository(firebaseService) }
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepository)
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                DashboardBottomNavigation(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
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
                DashboardScreen(
                    navController = navController,
                    adminChildViewModel = adminChildViewModel,
                    attendanceViewModel = attendanceViewModel,
                    weeklyPlanViewModel = weeklyPlanViewModel,
                    scoreViewModel = scoreViewModel
                )
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

            composable("raport_history/{childId}") { historyBackStackEntry ->
                val childId = historyBackStackEntry.arguments?.getString("childId").orEmpty()

                HistoryRaportScreen(
                    childId = childId,
                    adminChildViewModel = adminChildViewModel,
                    scoreViewModel = scoreViewModel,
                    weeklyPlanViewModel = weeklyPlanViewModel,
                    attendanceViewModel = attendanceViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("chat") {
                ChatScreen(
                    viewModel = chatViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    onEditProfileClick = {
                        navController.navigate("profile/edit")
                    },
                    onChangePasswordClick = {
                        navController.navigate("profile/password")
                    },
                    onAdminManagementClick = {
                        navController.navigate("profile/admin")
                    },
                    onLogoutClick = {
                        FirebaseAuth.getInstance().signOut()
                        loginViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("profile/edit") {
                EditProfileScreen(
                    profileViewModel = profileViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("profile/password") {
                ChangePasswordScreen(
                    profileViewModel = profileViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("profile/admin") {
                AdminManagementPage(
                    adminManagementViewModel = adminManagementViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
