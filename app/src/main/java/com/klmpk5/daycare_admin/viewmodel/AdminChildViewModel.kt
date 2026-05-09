package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdminChildViewModel(
    private val repository: ChildRepository
) : ViewModel() {

    // Tim UI tinggal observe variabel ini buat nampilin list anak
    val children: Flow<List<Child>> = repository.getAllChildrenLocal()

    init {
        // Otomatis narik SEMUA data anak dari Firebase pas ViewModel ini dibuka
        viewModelScope.launch {
            repository.syncAllChildrenFromRemote()
        }
    }

    // Tim UI tinggal panggil ini pas admin klik tombol "Simpan" di form tambah anak
    fun addChild(child: Child) {
        viewModelScope.launch {
            repository.addChild(child)
        }
    }
}

class AdminChildViewModelFactory(private val repository: ChildRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminChildViewModel::class.java)) {
            return AdminChildViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}