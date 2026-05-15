package com.klmpk5.daycare_admin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_plan_table")
data class WeeklyPlan(
    @PrimaryKey val planId: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val imageUrl: String? = null
)