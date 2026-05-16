package com.klmpk5.daycare_admin.data.remote.model

import com.klmpk5.daycare_admin.data.local.entities.User

data class UserRemoteDto(
    var uid: String = "",
    val email: String = "",
    val role: String = "",
    val fullName: String = "",
    val description: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toEntity(isSynced: Boolean = true): User {
        return User(
            uid = uid,
            email = email,
            role = role,
            fullName = fullName,
            description = description,
            updatedAt = updatedAt,
            isSynced = isSynced
        )
    }
}
