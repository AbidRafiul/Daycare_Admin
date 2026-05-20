package com.klmpk5.daycare_admin.repository

import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.remote.model.ChatMessageRemoteDto
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val firebaseService: FirebaseService) {

    fun getMessages(): Flow<List<ChatMessageRemoteDto>> {
        return firebaseService.getGlobalChats()
    }

    suspend fun sendMessage(message: ChatMessageRemoteDto) {
        firebaseService.sendChatMessage(message)
    }
}