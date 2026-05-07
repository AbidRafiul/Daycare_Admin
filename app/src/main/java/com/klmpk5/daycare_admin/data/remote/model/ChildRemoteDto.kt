package com.klmpk5.daycare_admin.data.remote.model

import com.klmpk5.daycare_admin.data.local.entities.Child

data class ChildRemoteDto(
    var childId: String = "", // WAJIB ADA: ID Unik dari Firebase
    val fullName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val parentUserId: String = "",
    val photoUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Fungsi untuk mengonversi ChildRemoteDto menjadi Child (Entity Room)
    fun toEntity(): Child {
        return Child(
            childId = this.childId, // Masukkan ke dalam mapping
            fullName = this.fullName,
            birthDate = this.birthDate,
            gender = this.gender,
            parentUserId = this.parentUserId,
            photoUrl = this.photoUrl,
            isActive = this.isActive,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}