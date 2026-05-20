package com.klmpk5.daycare_admin.data.remote.model

import com.klmpk5.daycare_admin.data.local.entities.User

data class UserRemoteDto(
    var uid: String = "",
    val email: String = "",
    val role: String = "",
    val fullName: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis(),
    val createdByEmail: String = "",
    val disabledAt: Long? = null,
    val disabledByEmail: String = "",
    val reactivatedAt: Long? = null,
    val reactivatedByEmail: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toEntity(isSynced: Boolean = true): User {
        return User(
            uid = uid,
            email = email,
            role = role,
            fullName = fullName,
            description = description,
            isActive = isActive,
            status = status,
            createdAt = createdAt,
            createdByEmail = createdByEmail,
            disabledAt = disabledAt,
            disabledByEmail = disabledByEmail,
            reactivatedAt = reactivatedAt,
            reactivatedByEmail = reactivatedByEmail,
            updatedAt = updatedAt,
            isSynced = isSynced
        )
    }
}
