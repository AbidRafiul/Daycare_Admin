package com.klmpk5.daycare_admin.data.remote.model

import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan

data class WeeklyPlanRemoteDto(
    var planId: String = "", // var agar bisa diisi ID otomatis dari Firebase
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val imageUrl: String? = null
) {
    fun toEntity(): WeeklyPlan {
        return WeeklyPlan(
            planId = this.planId,
            startDate = this.startDate,
            endDate = this.endDate,
            description = this.description,
            imageUrl = this.imageUrl
        )
    }
}