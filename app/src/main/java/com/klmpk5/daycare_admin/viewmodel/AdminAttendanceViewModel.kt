package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klmpk5.daycare_admin.data.local.entities.Attendance
import com.klmpk5.daycare_admin.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * State untuk proses simpan presensi.
 *
 * Idle    -> kondisi awal
 * Loading -> sedang menyimpan data
 * Success -> berhasil menyimpan data
 * Error   -> gagal menyimpan data
 */
sealed class AttendanceSaveState {
    object Idle : AttendanceSaveState()
    object Loading : AttendanceSaveState()
    object Success : AttendanceSaveState()
    data class Error(val message: String) : AttendanceSaveState()
}

/**
 * AttendanceViewModel digunakan untuk mengatur data presensi anak.
 *
 * ViewModel ini menjadi penghubung antara UI PresensiScreen dan AttendanceRepository.
 */
class AttendanceViewModel(
    private val repository: AttendanceRepository
) : ViewModel() {

    /**
     * selectedDate menyimpan tanggal presensi yang sedang dibuka.
     *
     * Format yang disarankan:
     * "2026-05-14"
     */
    private val selectedDate = MutableStateFlow("")

    /**
     * attendanceList akan otomatis mengambil data presensi berdasarkan selectedDate.
     *
     * Kalau selectedDate berubah, data presensi yang ditampilkan juga ikut berubah.
     */
    val attendanceList = selectedDate.flatMapLatest { date ->
        repository.getAttendanceByDateLocal(date)
    }

    private val _saveState = MutableStateFlow<AttendanceSaveState>(AttendanceSaveState.Idle)
    val saveState: StateFlow<AttendanceSaveState> = _saveState

    /**
     * Dipanggil ketika UI ingin mengganti tanggal presensi.
     *
     * Selain mengubah selectedDate, fungsi ini juga melakukan sync data dari Firestore
     * untuk tanggal tersebut ke database lokal.
     */
    fun setDate(date: String) {
        selectedDate.value = date

        viewModelScope.launch {
            repository.syncAttendanceByDate(date)
        }
    }

    /**
     * Menyimpan atau memperbarui presensi satu anak.
     *
     * Fungsi ini akan memanggil repository.addOrUpdateAttendance().
     * Di repository, data akan disimpan ke Room lokal dan Firestore.
     */
    fun saveAttendance(attendance: Attendance) {
        viewModelScope.launch {
            _saveState.value = AttendanceSaveState.Loading

            val success = repository.addOrUpdateAttendance(attendance)

            _saveState.value = if (success) {
                AttendanceSaveState.Success
            } else {
                AttendanceSaveState.Error("Gagal menyimpan presensi")
            }
        }
    }

    /**
     * Mengembalikan state simpan ke kondisi awal.
     * Berguna setelah snackbar/toast berhasil ditampilkan di UI.
     */
    fun resetSaveState() {
        _saveState.value = AttendanceSaveState.Idle
    }
}

/**
 * Factory untuk membuat AttendanceViewModel.
 *
 * Factory dibutuhkan karena AttendanceViewModel membutuhkan AttendanceRepository
 * sebagai parameter constructor.
 */
class AttendanceViewModelFactory(
    private val repository: AttendanceRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            return AttendanceViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}