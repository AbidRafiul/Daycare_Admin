package com.klmpk5.daycare_admin.data.remote.model

data class ChatMessageRemoteDto(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val role: String = "",
    val timestamp: Long = System.currentTimeMillis()
)