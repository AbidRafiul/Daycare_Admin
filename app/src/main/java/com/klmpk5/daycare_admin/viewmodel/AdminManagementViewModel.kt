package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminManagementViewModel(
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // State untuk memantau proses pendaftaran
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun registerNewAdmin(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _registerState.value = RegisterState.Error("Email dan password tidak boleh kosong")
            return
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                // 1. Buat akun di Auth
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val user = result.user

                if (user != null) {
                    // 2. Simpan role ke Firestore
                    firebaseService.saveUserProfile(
                        uid = user.uid,
                        email = email,
                        role = "admin"
                    )
                    _registerState.value = RegisterState.Success
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.localizedMessage ?: "Gagal membuat akun")
            }
        }
    }
}

// State class khusus untuk register
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

// Factory untuk AdminManagementViewModel
class AdminManagementViewModelFactory(private val firebaseService: FirebaseService) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminManagementViewModel::class.java)) {
            return AdminManagementViewModel(firebaseService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}