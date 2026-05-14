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

                val localChildren = remoteChildren.map { dto ->
                    Child(
                        childId = dto.childId,
                        childIdRemote = dto.childIdRemote,
                        fullName = dto.fullName,
                        nickName = dto.nickName,
                        birthDate = dto.birthDate,
                        gender = dto.gender,
                        parentUserId = dto.parentUserId,
                        parentEmail = dto.parentEmail,
                        photoUrl = dto.photoUrl,
                        isActive = dto.isActive,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt
                    )
                }

                childDao.insertAll(localChildren)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addChild(
        childEntity: Child,
        imageUri: Uri? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var finalPhotoUrl = childEntity.photoUrl

                if (imageUri != null) {
                    val uploadedUrl = cloudinaryService.uploadImage(imageUri)
                    if (uploadedUrl != null) {
                        finalPhotoUrl = uploadedUrl
                    }
                }

                val now = System.currentTimeMillis()

                val updatedChild = childEntity.copy(
                    parentEmail = childEntity.parentEmail
                        ?.trim()
                        ?.lowercase(),
                    parentUserId = null,
                    photoUrl = finalPhotoUrl,
                    updatedAt = now
                )

                childDao.insertChild(updatedChild)

                firebaseService.addChild(
                    updatedChild.toRemoteDto()
                )

                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun updateChild(
        childEntity: Child,
        imageUri: Uri? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var finalPhotoUrl = childEntity.photoUrl

                if (imageUri != null) {
                    val uploadedUrl = cloudinaryService.uploadImage(imageUri)
                    if (uploadedUrl != null) {
                        finalPhotoUrl = uploadedUrl
                    }
                }

                val now = System.currentTimeMillis()

                val updatedChild = childEntity.copy(
                    parentEmail = childEntity.parentEmail
                        ?.trim()
                        ?.lowercase(),
                    photoUrl = finalPhotoUrl,
                    updatedAt = now
                )

                childDao.updateChild(updatedChild)

                firebaseService.updateChild(
                    updatedChild.toRemoteDto()
                )

                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun softDeleteChild(child: Child): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val deletedChild = child.copy(
                    isActive = false,
                    updatedAt = System.currentTimeMillis()
                )

                childDao.updateChild(deletedChild)

                firebaseService.updateChild(
                    deletedChild.toRemoteDto()
                )

                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun Child.toRemoteDto(): ChildRemoteDto {
        return ChildRemoteDto(
            childId = childId,
            childIdRemote = childIdRemote,
            fullName = fullName,
            nickName = nickName,
            birthDate = birthDate,
            gender = gender,
            parentUserId = parentUserId,
            parentEmail = parentEmail,
            photoUrl = photoUrl,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}