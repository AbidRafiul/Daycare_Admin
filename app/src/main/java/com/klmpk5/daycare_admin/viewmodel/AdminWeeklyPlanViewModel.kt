package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_admin.repository.WeeklyPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

sealed class WeeklyPlanSaveState {
    object Idle : WeeklyPlanSaveState()
    object Loading : WeeklyPlanSaveState()
    object Success : WeeklyPlanSaveState()
    data class Error(val message: String) : WeeklyPlanSaveState()
}

class AdminWeeklyPlanViewModel(
    private val repository: WeeklyPlanRepository
) : ViewModel() {

    val weeklyPlans: Flow<List<WeeklyPlan>> = repository.getAllWeeklyPlansLocal()

    private val _saveState = kotlinx.coroutines.flow.MutableStateFlow<WeeklyPlanSaveState>(
        WeeklyPlanSaveState.Idle
    )
    val saveState: kotlinx.coroutines.flow.StateFlow<WeeklyPlanSaveState> = _saveState

    init {
        viewModelScope.launch {
            repository.syncWeeklyPlansFromRemote()
        }
    }

    fun addWeeklyPlan(plan: WeeklyPlan) {
        viewModelScope.launch {
            try {
                _saveState.value = WeeklyPlanSaveState.Loading
                repository.addWeeklyPlan(plan)
                _saveState.value = WeeklyPlanSaveState.Success
            } catch (e: Exception) {
                _saveState.value = WeeklyPlanSaveState.Error(
                    e.message ?: "Gagal menyimpan Weekly Plan"
                )
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = WeeklyPlanSaveState.Idle
    }
}

class AdminWeeklyPlanViewModelFactory(
    private val repository: WeeklyPlanRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminWeeklyPlanViewModel::class.java)) {
            return AdminWeeklyPlanViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}