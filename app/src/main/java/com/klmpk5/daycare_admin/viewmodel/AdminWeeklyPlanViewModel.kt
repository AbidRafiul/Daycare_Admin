package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_admin.repository.WeeklyPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdminWeeklyPlanViewModel(
    private val repository: WeeklyPlanRepository
) : ViewModel() {

    val weeklyPlans: Flow<List<WeeklyPlan>> = repository.getAllWeeklyPlansLocal()

    init {
        viewModelScope.launch {
            repository.syncWeeklyPlansFromRemote()
        }
    }

    fun addWeeklyPlan(plan: WeeklyPlan) {
        viewModelScope.launch {
            repository.addWeeklyPlan(plan)
        }
    }
}

class AdminWeeklyPlanViewModelFactory(private val repository: WeeklyPlanRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminWeeklyPlanViewModel::class.java)) {
            return AdminWeeklyPlanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}