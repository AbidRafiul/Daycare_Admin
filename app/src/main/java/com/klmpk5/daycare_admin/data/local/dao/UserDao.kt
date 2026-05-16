package com.klmpk5.daycare_admin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.klmpk5.daycare_admin.data.local.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("""
        SELECT * FROM users
        WHERE uid = :uid
        LIMIT 1
    """)
    fun observeUserById(uid: String): Flow<User?>

    @Query("""
        SELECT * FROM users
        WHERE uid = :uid
        LIMIT 1
    """)
    suspend fun getUserById(uid: String): User?

    @Query("""
        SELECT * FROM users
        WHERE isSynced = 0
    """)
    suspend fun getUnsyncedUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("""
        DELETE FROM users
    """)
    suspend fun deleteAllUsers()
}
