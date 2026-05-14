package com.klmpk5.daycare_admin.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klmpk5.daycare_admin.data.local.entities.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY childName ASC")
    fun getAttendanceByDate(date: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE childId = :childId ORDER BY date DESC")
    fun getAttendanceByChild(childId: String): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(attendanceList: List<Attendance>)

    @Query("DELETE FROM attendance WHERE attendanceId = :attendanceId")
    suspend fun deleteAttendance(attendanceId: String)
}