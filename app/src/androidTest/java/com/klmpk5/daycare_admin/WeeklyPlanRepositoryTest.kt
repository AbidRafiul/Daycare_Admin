package com.klmpk5.daycare_admin.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.klmpk5.daycare_admin.data.local.db.DaycareDatabase
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.remote.model.WeeklyPlanRemoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeeklyPlanRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: WeeklyPlanRepository
    private lateinit var firebaseService: FirebaseService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        try { FirebaseApp.initializeApp(context) } catch (e: Exception) {}

        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()

        database = Room.inMemoryDatabaseBuilder(context, DaycareDatabase::class.java).build()
        firebaseService = FirebaseService()
        repository = WeeklyPlanRepository(database.weeklyPlanDao(), firebaseService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAdminAddAndSyncWeeklyPlan() = runBlocking {
        withTimeout(20000) {
            withContext(Dispatchers.IO) {
                // 1. Admin membuat jadwal baru
                val plan = WeeklyPlan(
                    planId = "PLAN_ADMIN_001",
                    startDate = "10/05/2026",
                    endDate = "14/05/2026",
                    description = "Minggu Berkemah",
                    imageUrl = null
                )
                repository.addWeeklyPlan(plan)

                kotlinx.coroutines.delay(2000)

                // 2. Sinkronisasi (Menarik semua jadwal)
                repository.syncWeeklyPlansFromRemote()

                // 3. Verifikasi jadwal masuk ke Room
                val syncedLocal = repository.getAllWeeklyPlansLocal().first()
                assertTrue(syncedLocal.isNotEmpty())
                assertTrue(syncedLocal.any { it.description == "Minggu Berkemah" })
            }
        }
    }
}