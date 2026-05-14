package com.klmpk5.daycare_admin.repository

import com.klmpk5.daycare_admin.data.local.dao.AttendanceDao
import com.klmpk5.daycare_admin.data.local.entities.Attendance
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.remote.model.AttendanceRemoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val firebaseService: FirebaseService
) {
    fun getAttendanceByDateLocal(date: String): Flow<List<Attendance>> {
        return attendanceDao.getAttendanceByDate(date)
    }

    suspend fun syncAttendanceByDate(date: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteData = firebaseService.getAttendanceByDate(date)

                val localData = remoteData.map { dto ->
                    Attendance(
                        attendanceId = dto.attendanceId,
                        attendanceIdRemote = dto.attendanceIdRemote,
                        childId = dto.childId,
                        childName = dto.childName,
                        date = dto.date,
                        status = dto.status,
                        recordedBy = dto.recordedBy,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt
                    )
                }

                attendanceDao.insertAllAttendance(localData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addOrUpdateAttendance(attendance: Attendance): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val now = System.currentTimeMillis()

                val updatedAttendance = attendance.copy(
                    updatedAt = now
                )

                attendanceDao.insertAttendance(updatedAttendance)

                firebaseService.addOrUpdateAttendance(
                    updatedAttendance.toRemoteDto()
                )

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun Attendance.toRemoteDto(): AttendanceRemoteDto {
        return AttendanceRemoteDto(
            attendanceId = attendanceId,
            attendanceIdRemote = attendanceIdRemote,
            childId = childId,
            childName = childName,
            date = date,
            status = status,
            recordedBy = recordedBy,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}