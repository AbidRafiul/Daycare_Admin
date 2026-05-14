package com.klmpk5.daycare_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.ui.theme.Daycare_AdminTheme
import com.klmpk5.daycare_admin.ui.theme.navigation.AppNavigation
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModelFactory
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModelFactory
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel
import com.klmpk5.daycare_admin.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Daycare_AdminTheme {

                // Mengambil instance Application agar bisa akses repository yang sudah dibuat di App.kt
                val app = application as App

                // FirebaseService untuk LoginViewModel
                val firebaseService = FirebaseService()

                // ViewModel untuk login admin/guru
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(firebaseService)
                )

                // ViewModel untuk master data anak
                val adminChildViewModel: AdminChildViewModel = viewModel(
                    factory = AdminChildViewModelFactory(app.childRepository)
                )

                //Panggil ViewModel untuk presensi
                val attendanceViewModel: AttendanceViewModel = viewModel(
                    factory = AttendanceViewModelFactory(app.attendanceRepository)
                )

                // Navigasi utama aplikasi
                AppNavigation(
                    loginViewModel = loginViewModel,
                    adminChildViewModel = adminChildViewModel,
                    attendanceViewModel = attendanceViewModel

                )
            }
        }
    }
}