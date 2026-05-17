package com.klmpk5.daycare_admin.repository

import com.klmpk5.daycare_admin.data.local.dao.UserDao
import com.klmpk5.daycare_admin.data.local.entities.User
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val firebaseService: FirebaseService
) {

    fun observeUserLocal(uid: String): Flow<User?> {
        return userDao.observeUserById(uid)
    }

    suspend fun getUserLocal(uid: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(uid)
        }
    }

    suspend fun syncUserFromRemote(uid: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val remoteUser = firebaseService.getUserProfile(uid) ?: return@withContext null
                val localUser = remoteUser.toEntity(isSynced = true)

                userDao.insertUser(localUser)
                localUser
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun saveProfile(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            val localUser = user.copy(
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )

            userDao.insertUser(localUser)

            try {
                firebaseService.updateUserProfile(
                    uid = localUser.uid,
                    fullName = localUser.fullName,
                    email = localUser.email,
                    description = localUser.description,
                    role = localUser.role,
                    updatedAt = localUser.updatedAt
                )

                userDao.insertUser(localUser.copy(isSynced = true))
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun syncPendingUsers() {
        withContext(Dispatchers.IO) {
            val pendingUsers = userDao.getUnsyncedUsers()

            pendingUsers.forEach { user ->
                try {
                    firebaseService.updateUserProfile(
                        uid = user.uid,
                        fullName = user.fullName,
                        email = user.email,
                        description = user.description,
                        role = user.role,
                        updatedAt = user.updatedAt
                    )
                    userDao.insertUser(user.copy(isSynced = true))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
