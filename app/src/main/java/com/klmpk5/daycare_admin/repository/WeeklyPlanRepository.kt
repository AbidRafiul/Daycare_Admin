package com.klmpk5.daycare_admin.repository

import com.klmpk5.daycare_admin.data.local.dao.WeeklyPlanDao
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.remote.model.WeeklyPlanRemoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeeklyPlanRepository(
    private val weeklyPlanDao: WeeklyPlanDao,
    private val firebaseService: FirebaseService
) {
    // 1. Membaca data lokal untuk ditampilkan di UI (Wali Murid & Admin)
    fun getAllWeeklyPlansLocal(): Flow<List<WeeklyPlan>> {
        return weeklyPlanDao.getAllWeeklyPlans()
    }

    // 2. Sinkronisasi dari Firebase ke Room
    suspend fun syncWeeklyPlansFromRemote() {
        withContext(Dispatchers.IO) {
            try {
                val remotePlans = firebaseService.getAllWeeklyPlans()

                // Di sinilah fungsi toEntity() milik WeeklyPlanRemoteDto dipanggil (akan berubah biru!)
                val localPlans = remotePlans.map { it.toEntity() }

                localPlans.forEach { plan ->
                    weeklyPlanDao.insertWeeklyPlan(plan)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 3. Menambahkan Weekly Plan baru (Khusus Admin)
    suspend fun addWeeklyPlan(planEntity: WeeklyPlan) {
        withContext(Dispatchers.IO) {
            try {
                // Simpan ke Room
                weeklyPlanDao.insertWeeklyPlan(planEntity)

                // Konversi ke DTO untuk Firebase
                val remoteDto = WeeklyPlanRemoteDto(
                    planId = planEntity.planId,
                    startDate = planEntity.startDate,
                    endDate = planEntity.endDate,
                    description = planEntity.description,
                    imageUrl = planEntity.imageUrl
                )

                // Lempar ke Firebase
                firebaseService.addWeeklyPlan(remoteDto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}