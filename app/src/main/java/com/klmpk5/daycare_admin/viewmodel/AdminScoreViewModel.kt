package com.klmpk5.daycare_admin.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScoreSaveState {
    object Idle : ScoreSaveState
    object Loading : ScoreSaveState
    data class Success(val score: DailyScore) : ScoreSaveState
    data class Error(val message: String) : ScoreSaveState
}

class AdminScoreViewModel(
    private val repository: ScoreRepository
) : ViewModel() {
    private val _saveState = MutableStateFlow<ScoreSaveState>(ScoreSaveState.Idle)
    val saveState: StateFlow<ScoreSaveState> = _saveState.asStateFlow()

    // Tim UI butuh ngasih tau childId pas mau nampilin nilai
    fun getScores(childId: String): Flow<List<DailyScore>> {
        return repository.getScoresByChildLocal(childId)
    }

    fun getScoresByDate(date: String): Flow<List<DailyScore>> {
        return repository.getScoresByDateLocal(date)
    }

    // Dipanggil tim UI pas masuk ke halaman detail anak
    fun syncScores(childId: String) {
        viewModelScope.launch {
            repository.syncScoresFromRemote(childId)
        }
    }

    fun addScore(score: DailyScore, imageUri: Uri? = null) {
        viewModelScope.launch {
            _saveState.value = ScoreSaveState.Loading

            val result = repository.addScore(score, imageUri)
            _saveState.value = result.fold(
                onSuccess = { savedScore ->
                    ScoreSaveState.Success(savedScore)
                },
                onFailure = { error ->
                    ScoreSaveState.Error(error.message ?: "Nilai harian gagal disimpan")
                }
            )
        }
    }

    fun resetSaveState() {
        _saveState.value = ScoreSaveState.Idle
    }
}

class AdminScoreViewModelFactory(private val repository: ScoreRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminScoreViewModel::class.java)) {
            return AdminScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
