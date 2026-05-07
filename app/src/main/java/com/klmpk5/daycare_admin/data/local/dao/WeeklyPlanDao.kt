package com.klmpk5.daycare_admin.data.local.dao

import androidx.room.*
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyPlanDao {
    @Query("SELECT * FROM weekly_plan_table ORDER BY startDate DESC")
    fun getAllWeeklyPlans(): Flow<List<WeeklyPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyPlan(plan: WeeklyPlan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyPlans(plans: List<WeeklyPlan>)

    @Update
    suspend fun updateWeeklyPlan(plan: WeeklyPlan)

    @Delete
    suspend fun deleteWeeklyPlan(plan: WeeklyPlan)
}