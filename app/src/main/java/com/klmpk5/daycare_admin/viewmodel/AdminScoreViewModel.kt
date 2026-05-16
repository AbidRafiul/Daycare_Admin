package com.klmpk5.daycare_admin.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdminScoreViewModel(
    private val repository: ScoreRepository
) : ViewModel() {

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
            repository.addScore(score, imageUri)
        }
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
