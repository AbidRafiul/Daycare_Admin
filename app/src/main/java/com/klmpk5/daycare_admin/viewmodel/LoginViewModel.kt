package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Status untuk mengontrol UI
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // Fungsi Login pakai Email & Password manual
    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("Email dan password tidak boleh kosong")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val user = result.user

                if (user != null) {
                    val role = firebaseService.getUserRole(user.uid)
                    if (role == "admin") {
                        _loginState.value = LoginState.Success
                    } else {
                        auth.signOut()
                        _loginState.value = LoginState.Error("Akses Ditolak: Akun ini bukan Admin!")
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Terjadi kesalahan saat login")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}