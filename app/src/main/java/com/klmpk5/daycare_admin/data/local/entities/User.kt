package com.klmpk5.daycare_admin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val email: String,
    val role: String = "admin",
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
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)
