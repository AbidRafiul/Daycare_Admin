package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService

/**
 * Factory untuk membuat LoginViewModel.
 *
 * Kenapa butuh Factory?
 * Karena LoginViewModel punya parameter FirebaseService,
 * jadi tidak bisa langsung dibuat otomatis oleh viewModel() biasa.
 */
class LoginViewModelFactory(
    private val firebaseService: FirebaseService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(firebaseService) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}