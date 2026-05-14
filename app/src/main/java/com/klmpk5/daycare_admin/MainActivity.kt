package com.klmpk5.daycare_admin

import android.os.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.ui.theme.navigation.AppNavigation
import com.klmpk5.daycare_admin.ui.theme.Daycare_AdminTheme
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel
import com.klmpk5.daycare_admin.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Daycare_AdminTheme {

                // Membuat service Firebase untuk dipakai oleh ViewModel
                val firebaseService = FirebaseService()

                // Membuat LoginViewModel dengan bantuan Factory
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(firebaseService)
                )

                // Menjalankan navigasi utama aplikasi
                AppNavigation(
                    loginViewModel = loginViewModel
                )
            }
        }
    }
}