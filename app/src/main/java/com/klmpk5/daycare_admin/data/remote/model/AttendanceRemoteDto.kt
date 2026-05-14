package com.klmpk5.daycare_admin.data.remote.model
data class AttendanceRemoteDto(
    var attendanceId: String = "",
    var attendanceIdRemote: String? = null,

    var childId: String = "",
    var childName: String = "",
    var date: String = "",
    var status: String = "",

    var recordedBy: String? = null,

    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)