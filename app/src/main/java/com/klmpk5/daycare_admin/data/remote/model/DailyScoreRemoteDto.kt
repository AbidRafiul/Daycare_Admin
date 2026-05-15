package com.klmpk5.daycare_admin.data.remote.model

import com.klmpk5.daycare_admin.data.local.entities.DailyScore

data class DailyScoreRemoteDto(
    var scoreId: String = "",
    val childId: String = "",
    val date: String = "",
    val activityName: String = "",
    val score: Int = 0,
    val notes: String? = null,
    val imageUrl: String? = null
) {
    fun toEntity(): DailyScore {
        return DailyScore(
            scoreId = this.scoreId,
            childId = this.childId,
            date = this.date,
            activityName = this.activityName,
            score = this.score,
            notes = this.notes,
            imageUrl = this.imageUrl
        )
    }
}
