package com.klmpk5.daycare_admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_admin.data.remote.model.ChatMessageRemoteDto
import com.klmpk5.daycare_admin.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessageRemoteDto>>(emptyList())
    val messages: StateFlow<List<ChatMessageRemoteDto>> = _messages

    init {
        viewModelScope.launch {
            repository.getMessages().collect { listPesan ->
                _messages.value = listPesan
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val user = auth.currentUser ?: return

        viewModelScope.launch {
            val newMessage = ChatMessageRemoteDto(
                text = text,
                senderId = user.uid,
                senderName = user.displayName ?: "Admin Guru",
                role = "admin",
                timestamp = System.currentTimeMillis()
            )

            try {
                repository.sendMessage(newMessage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}