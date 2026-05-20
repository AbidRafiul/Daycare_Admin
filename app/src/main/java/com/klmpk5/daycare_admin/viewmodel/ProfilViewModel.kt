package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.UserProfileChangeRequest
import com.klmpk5.daycare_admin.data.local.entities.User
import com.klmpk5.daycare_admin.repository.UserRepository
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

data class ProfileUiState(
    val fullName: String = "Admin Daycare",
    val email: String = "",
    val description: String = "",
    val role: String = "Admin / Guru",
    val isSynced: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class ProfileSaveState {
    object Idle : ProfileSaveState()
    object Loading : ProfileSaveState()
    data class Success(val message: String) : ProfileSaveState()
    data class Error(val message: String) : ProfileSaveState()
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState

    private val _profileSaveState = MutableStateFlow<ProfileSaveState>(ProfileSaveState.Idle)
    val profileSaveState: StateFlow<ProfileSaveState> = _profileSaveState

    private val _passwordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val passwordState: StateFlow<PasswordState> = _passwordState

    fun loadProfile() {
        viewModelScope.launch {
            val user = auth.currentUser

            if (user == null) {
                _profileState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = "User tidak ditemukan"
                )
                return@launch
            }

            _profileState.value = _profileState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val localProfile = userRepository.getUserLocal(user.uid)
                if (localProfile != null) {
                    _profileState.value = localProfile.toProfileUiState(
                        authEmail = user.email.orEmpty()
                    )
                }

                val profile = if (localProfile?.isSynced == false) {
                    userRepository.syncPendingUsers()
                    userRepository.getUserLocal(user.uid) ?: localProfile
                } else {
                    userRepository.syncUserFromRemote(user.uid) ?: localProfile
                }
                val email = profile?.email
                    ?.takeIf { it.isNotBlank() }
                    ?: user.email.orEmpty()
                val fullName = profile?.fullName
                    ?.takeIf { it.isNotBlank() }
                    ?: user.displayName
                    ?: "Admin Daycare"
                val role = when (profile?.role) {
                    "admin" -> "Admin / Guru"
                    null, "" -> "Admin / Guru"
                    else -> profile?.role.orEmpty()
                }

                _profileState.value = ProfileUiState(
                    fullName = fullName,
                    email = email,
                    description = profile?.description.orEmpty(),
                    role = role,
                    isSynced = profile?.isSynced ?: true,
                    isLoading = false
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Gagal memuat profil"
                )
            }
        }
    }

    fun updateProfile(
        fullName: String,
        email: String,
        description: String
    ) {
        val cleanFullName = fullName.trim()
        val cleanEmail = email.trim()
        val cleanDescription = description.trim()

        if (cleanFullName.isBlank()) {
            _profileSaveState.value = ProfileSaveState.Error("Nama tidak boleh kosong")
            return
        }

        if (cleanEmail.isBlank()) {
            _profileSaveState.value = ProfileSaveState.Error("Email tidak boleh kosong")
            return
        }

        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            _profileSaveState.value = ProfileSaveState.Error("Format email belum valid")
            return
        }

        viewModelScope.launch {
            try {
                _profileSaveState.value = ProfileSaveState.Loading

                val user = auth.currentUser
                if (user == null) {
                    _profileSaveState.value = ProfileSaveState.Error("User tidak ditemukan")
                    return@launch
                }

                val role = _profileState.value.role
                    .takeIf { it.isNotBlank() && it != "Admin / Guru" }
                    ?: "admin"
                val localUser = User(
                    uid = user.uid,
                    email = cleanEmail,
                    role = role,
                    fullName = cleanFullName,
                    description = cleanDescription,
                    isSynced = false
                )
                val isRemoteSynced = userRepository.saveProfile(localUser)

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(cleanFullName)
                    .build()

                try {
                    if (cleanEmail != user.email.orEmpty()) {
                        user.updateEmail(cleanEmail).await()
                    }
                    user.updateProfile(profileUpdates).await()
                } catch (e: FirebaseAuthRecentLoginRequiredException) {
                    _profileState.value = _profileState.value.copy(
                        fullName = cleanFullName,
                        email = cleanEmail,
                        description = cleanDescription,
                        isSynced = isRemoteSynced,
                        isLoading = false,
                        errorMessage = null
                    )
                    _profileSaveState.value = ProfileSaveState.Success(
                        "Profil tersimpan. Untuk mengubah email login, logout lalu login lagi"
                    )
                    return@launch
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                _profileState.value = _profileState.value.copy(
                    fullName = cleanFullName,
                    email = cleanEmail,
                    description = cleanDescription,
                    isSynced = isRemoteSynced,
                    isLoading = false,
                    errorMessage = null
                )
                _profileSaveState.value = ProfileSaveState.Success(
                    if (isRemoteSynced) {
                        "Profil berhasil diperbarui"
                    } else {
                        "Profil tersimpan offline, akan disinkronkan saat online"
                    }
                )
            } catch (e: Exception) {
                _profileSaveState.value = ProfileSaveState.Error(
                    e.localizedMessage ?: "Gagal menyimpan profil"
                )
            }
        }
    }

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

    fun resetProfileSaveState() {
        _profileSaveState.value = ProfileSaveState.Idle
    }

    private fun User.toProfileUiState(authEmail: String): ProfileUiState {
        return ProfileUiState(
            fullName = fullName.ifBlank { "Admin Daycare" },
            email = email.ifBlank { authEmail },
            description = description,
            role = if (role == "admin") "Admin / Guru" else role,
            isSynced = isSynced,
            isLoading = false
        )
    }
}

class ProfileViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
