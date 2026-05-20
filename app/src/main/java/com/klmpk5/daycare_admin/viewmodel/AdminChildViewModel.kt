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

    fun addChild(child: Child, imageUri: Uri? = null) {
        viewModelScope.launch {
            repository.addChild(child.withNormalizedParentEmail(), imageUri)
        }
    }

    fun updateChild(child: Child, imageUri: Uri? = null) {
        viewModelScope.launch {
            repository.updateChild(child.withNormalizedParentEmail(), imageUri)
        }
    }

    fun softDeleteChild(child: Child) {
        viewModelScope.launch {
            repository.softDeleteChild(child)
        }
    }

    private fun Child.withNormalizedParentEmail(): Child {
        return copy(
            parentEmail = parentEmail
                ?.trim()
                ?.lowercase()
                ?.ifBlank { null }
        )
    }
}

class AdminChildViewModelFactory(
    private val repository: ChildRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminChildViewModel::class.java)) {
            return AdminChildViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
