package com.klmpk5.daycare_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.ui.theme.navigation.AppNavigation
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModelFactory
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModelFactory
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModelFactory
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel
import com.klmpk5.daycare_admin.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val app = application as App
            val firebaseService = FirebaseService()

            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(firebaseService)
            )

            val adminChildViewModel: AdminChildViewModel = viewModel(
                factory = AdminChildViewModelFactory(app.childRepository)
            )

            val attendanceViewModel: AttendanceViewModel = viewModel(
                factory = AttendanceViewModelFactory(app.attendanceRepository)
            )

            val weeklyPlanViewModel: AdminWeeklyPlanViewModel = viewModel(
                factory = AdminWeeklyPlanViewModelFactory(app.weeklyPlanRepository)
            )

            AppNavigation(
                loginViewModel = loginViewModel,
                adminChildViewModel = adminChildViewModel,
                attendanceViewModel = attendanceViewModel,
                weeklyPlanViewModel = weeklyPlanViewModel
            )
            }
        }
    }
