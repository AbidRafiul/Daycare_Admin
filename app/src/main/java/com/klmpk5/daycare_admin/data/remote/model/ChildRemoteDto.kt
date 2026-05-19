package com.klmpk5.daycare_admin.data.remote.model

data class ChildRemoteDto(
    var childId: String = "",
    var childIdRemote: String? = null,

    var fullName: String = "",
    var nickName: String? = null,
    var birthDate: String = "",
    var gender: String = "",

    var parentEmail: String? = null,

    var photoUrl: String? = null,
    var isActive: Boolean = true,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)
