package com.klmpk5.daycare_admin.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.klmpk5.daycare_admin.data.local.db.DaycareDatabase
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
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

class ScoreRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: ScoreRepository
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
        repository = ScoreRepository(database.scoreDao(), firebaseService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAdminAddAndSyncScore() = runBlocking {
        withTimeout(20000) {
            withContext(Dispatchers.IO) {
                // 1. Admin memberikan nilai ke anak tertentu
                val score = DailyScore(
                    scoreId = "SCORE_ADMIN_001",
                    childId = "CHILD_TEST_X",
                    date = "10/05/2026",
                    activityName = "Menyanyi",
                    score = "A",
                    notes = "Sangat berbakat"
                )
                repository.addScore(score)

                kotlinx.coroutines.delay(2000)

                // 2. Sinkronisasi nilai untuk anak tersebut
                repository.syncScoresFromRemote("CHILD_TEST_X")

                // 3. Verifikasi nilai masuk ke Room
                val syncedLocal = repository.getScoresByChildLocal("CHILD_TEST_X").first()
                assertTrue(syncedLocal.isNotEmpty())
                assertTrue(syncedLocal.any { it.activityName == "Menyanyi" })
            }
        }
    }
}