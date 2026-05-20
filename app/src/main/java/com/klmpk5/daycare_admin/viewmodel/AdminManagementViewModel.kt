package com.klmpk5.daycare_admin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.remote.model.UserRemoteDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminManagementUiState(
    val admins: List<UserRemoteDto> = emptyList(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val disablingAdminUid: String? = null,
    val reactivatingAdminUid: String? = null,
    val message: String? = null,
    val errorMessage: String? = null
)

class AdminManagementViewModel(
    private val firebaseService: FirebaseService,
    private val appContext: Context
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AdminManagementUiState())
    val uiState: StateFlow<AdminManagementUiState> = _uiState.asStateFlow()

    init {
        loadAdmins()
    }

    fun loadAdmins() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null, errorMessage = null)

            try {
                _uiState.value = _uiState.value.copy(
                    admins = firebaseService.getAllAdmins(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Gagal memuat data admin"
                )
            }
        }
    }

    fun registerNewAdmin(
        email: String,
        pass: String,
        fullName: String,
        description: String
    ) {
        val cleanEmail = email.trim().lowercase()
        val cleanFullName = fullName.trim()
        val cleanDescription = description.trim()

        if (cleanEmail.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email dan password tidak boleh kosong")
            return
        }

        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            _uiState.value = _uiState.value.copy(errorMessage = "Format email belum valid")
            return
        }

        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password minimal 6 karakter")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, message = null, errorMessage = null)

            try {
                val secondaryAuth = getSecondaryAuth()
                val result = secondaryAuth.createUserWithEmailAndPassword(cleanEmail, pass).await()
                val newUser = result.user ?: throw IllegalStateException("Akun admin gagal dibuat")
                val creatorEmail = auth.currentUser?.email.orEmpty()
                val now = System.currentTimeMillis()

                firebaseService.saveUserProfile(
                    uid = newUser.uid,
                    email = cleanEmail,
                    role = "admin",
                    fullName = cleanFullName,
                    description = cleanDescription,
                    isActive = true,
                    createdAt = now,
                    createdByEmail = creatorEmail,
                    updatedAt = now
                )

                secondaryAuth.signOut()

                _uiState.value = _uiState.value.copy(
                    admins = firebaseService.getAllAdmins(),
                    isCreating = false,
                    message = "Akun admin berhasil ditambahkan oleh $creatorEmail"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    errorMessage = e.localizedMessage ?: "Gagal membuat akun admin"
                )
            }
        }
    }

    fun deactivateAdmin(admin: UserRemoteDto) {
        val currentUid = auth.currentUser?.uid.orEmpty()
        val currentEmail = auth.currentUser?.email.orEmpty()

        if (admin.uid == currentUid) {
            _uiState.value = _uiState.value.copy(errorMessage = "Akun yang sedang digunakan tidak bisa dinonaktifkan")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                disablingAdminUid = admin.uid,
                message = null,
                errorMessage = null
            )

            try {
                firebaseService.deactivateAdmin(
                    uid = admin.uid,
                    disabledByEmail = currentEmail
                )

                _uiState.value = _uiState.value.copy(
                    admins = firebaseService.getAllAdmins(),
                    disablingAdminUid = null,
                    message = "Akun ${admin.email} dinonaktifkan oleh $currentEmail"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    disablingAdminUid = null,
                    errorMessage = e.localizedMessage ?: "Gagal menonaktifkan admin"
                )
            }
        }
    }

    fun reactivateAdmin(admin: UserRemoteDto) {
        val currentEmail = auth.currentUser?.email.orEmpty()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                reactivatingAdminUid = admin.uid,
                message = null,
                errorMessage = null
            )

            try {
                firebaseService.reactivateAdmin(
                    uid = admin.uid,
                    reactivatedByEmail = currentEmail
                )

                _uiState.value = _uiState.value.copy(
                    admins = firebaseService.getAllAdmins(),
                    reactivatingAdminUid = null,
                    message = "Akun ${admin.email} diaktifkan kembali oleh $currentEmail"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    reactivatingAdminUid = null,
                    errorMessage = e.localizedMessage ?: "Gagal mengaktifkan admin"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, errorMessage = null)
    }

    private fun getSecondaryAuth(): FirebaseAuth {
        val appName = "admin_creator"
        val secondaryApp = try {
            FirebaseApp.getInstance(appName)
        } catch (e: IllegalStateException) {
            FirebaseApp.initializeApp(
                appContext,
                FirebaseApp.getInstance().options,
                appName
            ) ?: throw IllegalStateException("Firebase secondary app gagal dibuat")
        }

        return FirebaseAuth.getInstance(secondaryApp)
    }
}

class AdminManagementViewModelFactory(
    private val firebaseService: FirebaseService,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminManagementViewModel::class.java)) {
            return AdminManagementViewModel(firebaseService, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
