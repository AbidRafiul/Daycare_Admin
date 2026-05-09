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

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("Email dan password tidak boleh kosong")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                // 1. Coba login ke Firebase Auth
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val user = result.user

                if (user != null) {
                    // 2. Jika login berhasil, cek role-nya di Firestore
                    val role = firebaseService.getUserRole(user.uid)

                    if (role == "admin") {
                        // 3. Jika benar admin, izinkan masuk
                        _loginState.value = LoginState.Success
                    } else {
                        // 4. Jika bukan admin (misal orang tua coba login ke app admin)
                        auth.signOut() // Keluarkan secara paksa
                        _loginState.value = LoginState.Error("Akses Ditolak: Akun ini bukan Admin!")
                    }
                }
            } catch (e: Exception) {
                // Tangkap error jika password salah atau email tidak terdaftar
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Terjadi kesalahan saat login")
            }
        }
    }
}