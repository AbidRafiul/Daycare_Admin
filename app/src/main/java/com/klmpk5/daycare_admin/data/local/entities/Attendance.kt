package com.klmpk5.daycare_admin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey val attendanceId: String,

    val attendanceIdRemote: String? = null,

    val childId: String,
    val childName: String,
    val date: String,
    val status: String,

    val recordedBy: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)