package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class PasswordState {
    object Idle : PasswordState()
    object Loading : PasswordState()
    object Success : PasswordState()
    data class Error(val message: String) : PasswordState()
}

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _passwordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val passwordState: StateFlow<PasswordState> = _passwordState

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _passwordState.value = PasswordState.Error("Semua field password wajib diisi")
            return
        }

        if (newPassword.length < 6) {
            _passwordState.value = PasswordState.Error("Password baru minimal 6 karakter")
            return
        }

        if (newPassword != confirmPassword) {
            _passwordState.value = PasswordState.Error("Konfirmasi password tidak sama")
            return
        }

        viewModelScope.launch {
            try {
                _passwordState.value = PasswordState.Loading

                val user = auth.currentUser
                val email = user?.email

                if (user == null || email.isNullOrBlank()) {
                    _passwordState.value = PasswordState.Error("User tidak ditemukan")
                    return@launch
                }

                val credential = EmailAuthProvider.getCredential(email, oldPassword)

                // Verifikasi ulang akun sebelum ubah password
                user.reauthenticate(credential).await()

                // Update password di Firebase Auth
                user.updatePassword(newPassword).await()

                _passwordState.value = PasswordState.Success
            } catch (e: Exception) {
                _passwordState.value = PasswordState.Error(
                    e.localizedMessage ?: "Gagal mengubah password"
                )
            }
        }
    }

    fun resetState() {
        _passwordState.value = PasswordState.Idle
    }
}