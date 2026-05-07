package com.klmpk5.daycare_admin.repository

import com.klmpk5.daycare_admin.data.local.dao.ChildDao
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.local.entities.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChildRepository(
    private val childDao: ChildDao,
    private val firebaseService: FirebaseService
) {
    // 1. MEMBACA DATA LOKAL
    fun getAllChildrenLocal(): Flow<List<Child>> {
        return childDao.getAllChildren()
    }

    // 2. SINKRONISASI SEMUA ANAK DARI FIREBASE (Akses Admin)
    suspend fun syncAllChildrenFromRemote() {
        withContext(Dispatchers.IO) {
            try {
                // Admin menarik semua data tanpa filter parentUserId
                val remoteChildren = firebaseService.getAllChildren()
                val localChildren = remoteChildren.map { it.toEntity() }

                localChildren.forEach { child ->
                    childDao.insertChild(child)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 3. MENAMBAH ANAK BARU
    suspend fun addChild(childEntity: Child) {
        withContext(Dispatchers.IO) {
            try {
                childDao.insertChild(childEntity)

                val remoteDto = com.klmpk5.daycare_admin.data.remote.model.ChildRemoteDto(
                    childId = childEntity.childId,
                    fullName = childEntity.fullName,
                    birthDate = childEntity.birthDate,
                    gender = childEntity.gender,
                    parentUserId = childEntity.parentUserId,
                    photoUrl = childEntity.photoUrl,
                    isActive = childEntity.isActive,
                    createdAt = childEntity.createdAt,
                    updatedAt = childEntity.updatedAt
                )

                firebaseService.addChild(remoteDto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}