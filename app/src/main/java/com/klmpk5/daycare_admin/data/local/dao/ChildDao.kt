package com.klmpk5.daycare_admin.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.klmpk5.daycare_admin.data.local.entities.Child
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {

    @Query("""
        SELECT * FROM children
        WHERE isActive = 1
        ORDER BY fullName ASC
    """)
    fun getAllChildren(): Flow<List<Child>>

    @Query("""
        SELECT * FROM children
        WHERE childId = :childId
        LIMIT 1
    """)
    suspend fun getChildById(childId: String): Child?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(children: List<Child>)

    @Update
    suspend fun updateChild(child: Child)

    @Delete
    suspend fun deleteChild(child: Child)

    @Query("""
        DELETE FROM children
    """)
    suspend fun deleteAllChildren()
}