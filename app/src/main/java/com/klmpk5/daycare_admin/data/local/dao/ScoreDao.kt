package com.klmpk5.daycare_admin.data.local.dao

import androidx.room.*
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM daily_score_table WHERE childId = :childId ORDER BY date DESC")
    fun getScoresByChildId(childId: String): Flow<List<DailyScore>>

    @Query("SELECT * FROM daily_score_table WHERE date = :date")
    fun getScoresByDate(date: String): Flow<List<DailyScore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: DailyScore): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScores(scores: List<DailyScore>)

    @Update
    suspend fun updateScore(score: DailyScore)

    @Delete
    suspend fun deleteScore(score: DailyScore)
}
