package com.klmpk5.daycare_admin.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.klmpk5.daycare_admin.data.local.db.DaycareDatabase
import com.klmpk5.daycare_admin.data.remote.CloudinaryService
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

class ChildRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: ChildRepository
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
        repository = ChildRepository(database.childDao(), firebaseService, CloudinaryService())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAdminSyncAllChildren() = runBlocking {
        withTimeout(20000) {
            withContext(Dispatchers.IO) {
                // 1. Admin menyiapkan data dummy dengan parent yang BERBEDA-BEDA
                val child1 = com.klmpk5.daycare_admin.data.remote.model.ChildRemoteDto(
                    childId = "ADMIN_TEST_01", fullName = "Anak A", parentEmail = "parent-x@email.com"
                )
                val child2 = com.klmpk5.daycare_admin.data.remote.model.ChildRemoteDto(
                    childId = "ADMIN_TEST_02", fullName = "Anak B", parentEmail = "parent-y@email.com"
                )

                firebaseService.addChild(child1)
                firebaseService.addChild(child2)

                // Beri waktu Firebase indexing
                kotlinx.coroutines.delay(2000)

                // 2. Admin melakukan sync keseluruhan (tanpa parameter orang tua)
                repository.syncAllChildrenFromRemote()

                // 3. Verifikasi: Admin harusnya mendapatkan KEDUA anak tersebut
                val syncedLocal = repository.getAllChildrenLocal().first()
                assertTrue(syncedLocal.size >= 2) // Harusnya berisi Anak A dan Anak B
                assertTrue(syncedLocal.any { it.fullName == "Anak A" })
                assertTrue(syncedLocal.any { it.fullName == "Anak B" })
            }
        }
    }
}
