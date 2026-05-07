package com.klmpk5.daycare_admin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey val childId: String,
    val childIdRemote: String? = null,
    val fullName: String,
    val nickName: String? = null,
    val birthDate: String,
    val gender: String,
    val parentUserId: String,
    val photoUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)