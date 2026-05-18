package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
// FITUR BARU: Import GoogleAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
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

    // FITUR BARU: State khusus untuk Lupa Password
    private val _resetPasswordState = MutableStateFlow<LoginState>(LoginState.Idle)
    val resetPasswordState: StateFlow<LoginState> = _resetPasswordState

    // FUNGSI LAMA: Login pakai Email & Password manual
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

    // FUNGSI LAMA: Fungsi Login pakai Akun Google
    fun loginWithGoogle(idToken: String) {
        _loginState.value = LoginState.Loading

        // Ubah token dari Google menjadi Kredensial Firebase
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        viewModelScope.launch {
            try {
                // 1. Login ke Firebase Auth
                val result = auth.signInWithCredential(credential).await()
                val user = result.user

                if (user != null) {
                    // 2. Cek apakah role-nya admin di Firestore
                    val role = firebaseService.getUserRole(user.uid)

                    if (role == "admin") {
                        _loginState.value = LoginState.Success
                    } else {
                        auth.signOut() // Tendang kalau bukan admin
                        _loginState.value = LoginState.Error("Akses Ditolak: Akun Google ini bukan Admin!")
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Gagal login via Google")
            }
        }
    }

    // FITUR BARU: Fungsi mengirim email Lupa Password
    fun resetPassword(emailReset: String) {
        if (emailReset.isBlank()) {
            _resetPasswordState.value = LoginState.Error("Email tidak boleh kosong!")
            return
        }

        _resetPasswordState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                // Meminta Firebase mengirimkan email reset
                auth.sendPasswordResetEmail(emailReset).await()
                _resetPasswordState.value = LoginState.Success
            } catch (e: Exception) {
                _resetPasswordState.value = LoginState.Error(e.localizedMessage ?: "Gagal mengirim email reset password. Pastikan email terdaftar.")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    fun clearResetState() {
        _resetPasswordState.value = LoginState.Idle
    }
}