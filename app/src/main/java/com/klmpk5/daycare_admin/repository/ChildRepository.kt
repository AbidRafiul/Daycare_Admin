package com.klmpk5.daycare_admin.repository

import android.net.Uri
import com.klmpk5.daycare_admin.data.local.dao.ChildDao
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.data.remote.CloudinaryService
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.remote.model.ChildRemoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChildRepository(
    private val childDao: ChildDao,
    private val firebaseService: FirebaseService,
    private val cloudinaryService: CloudinaryService
) {

    fun getAllChildrenLocal(): Flow<List<Child>> {
        return childDao.getAllChildren()
    }

    suspend fun syncAllChildrenFromRemote() {
        withContext(Dispatchers.IO) {
            try {
                val remoteChildren = firebaseService.getAllChildren()
                val localEntities = remoteChildren.map { dto ->
                    Child(
                        childId = dto.childId,
                        fullName = dto.fullName,
                        birthDate = dto.birthDate,
                        gender = dto.gender,
                        parentUserId = dto.parentUserId,
                        photoUrl = dto.photoUrl,
                        isActive = dto.isActive,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt
                    )
                }
                childDao.insertAll(localEntities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addChild(childEntity: Child, imageUri: Uri? = null) {
        withContext(Dispatchers.IO) {
            try {
                var finalPhotoUrl = childEntity.photoUrl

                // 1. Upload ke Cloudinary jika ada gambar yang dipilih
                if (imageUri != null) {
                    val uploadedUrl = cloudinaryService.uploadImage(imageUri)
                    if (uploadedUrl != null) {
                        finalPhotoUrl = uploadedUrl
                    }
                }

                // 2. Buat duplikat data dengan URL gambar terbaru
                val updatedChild = childEntity.copy(photoUrl = finalPhotoUrl)

                // 3. Simpan ke database lokal (Room)
                childDao.insertChild(updatedChild)

                // 4. Simpan ke Firebase Firestore
                val remoteDto = ChildRemoteDto(
                    childId = updatedChild.childId,
                    fullName = updatedChild.fullName,
                    birthDate = updatedChild.birthDate,
                    gender = updatedChild.gender,
                    parentUserId = updatedChild.parentUserId,
                    photoUrl = updatedChild.photoUrl,
                    isActive = updatedChild.isActive,
                    createdAt = updatedChild.createdAt,
                    updatedAt = updatedChild.updatedAt
                )
                firebaseService.addChild(remoteDto)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}