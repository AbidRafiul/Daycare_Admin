package com.klmpk5.daycare_admin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_score_table")
data class DailyScore(
    @PrimaryKey val scoreId: String,
    val childId: String,
    val date: String,
    val activityName: String,
    val score: Int,
    val notes: String?,
    val imageUrl: String? = null
)
