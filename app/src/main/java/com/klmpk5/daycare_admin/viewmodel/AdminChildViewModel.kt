package com.klmpk5.daycare_admin.viewmodel

import android.net.Uri
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

    val children: Flow<List<Child>> = repository.getAllChildrenLocal()

    init {
        viewModelScope.launch {
            repository.syncAllChildrenFromRemote()
        }
    }

    // Fungsi ini sekarang siap menerima Uri gambar dari tim UI
    fun addChild(child: Child, imageUri: Uri? = null) {
        viewModelScope.launch {
            repository.addChild(child, imageUri)
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