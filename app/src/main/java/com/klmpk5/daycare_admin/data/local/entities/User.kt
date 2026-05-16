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
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)
