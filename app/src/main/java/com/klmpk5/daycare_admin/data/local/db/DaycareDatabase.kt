package com.klmpk5.daycare_admin.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klmpk5.daycare_admin.data.local.dao.ChildDao
import com.klmpk5.daycare_admin.data.local.dao.ScoreDao
import com.klmpk5.daycare_admin.data.local.dao.WeeklyPlanDao
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan

@Database(
    entities = [
        Child::class,
        WeeklyPlan::class,
        DailyScore::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DaycareDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao
    abstract fun scoreDao(): ScoreDao
}