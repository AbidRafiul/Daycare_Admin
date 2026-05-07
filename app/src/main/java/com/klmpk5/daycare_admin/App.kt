package com.klmpk5.daycare_admin

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.klmpk5.daycare_admin.data.local.db.DaycareDatabase
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.repository.ChildRepository
import com.klmpk5.daycare_admin.repository.ScoreRepository
import com.klmpk5.daycare_admin.repository.WeeklyPlanRepository

class App : Application() {
    // 1. Inisialisasi Firebase Service
    private val firebaseService by lazy { FirebaseService() }

    // 2. Inisialisasi Room Database Lokal
    val database by lazy {
        Room.databaseBuilder(
            this,
            DaycareDatabase::class.java,
            "daycare_admin_db"
        )
            // Opsi fallback jika ada perubahan struktur tabel di masa depan
            .fallbackToDestructiveMigration()
            .build()
    }

    // 3. Inisialisasi Semua Repository
    val childRepository by lazy { ChildRepository(database.childDao(), firebaseService) }
    val weeklyPlanRepository by lazy { WeeklyPlanRepository(database.weeklyPlanDao(), firebaseService) }
    val scoreRepository by lazy { ScoreRepository(database.scoreDao(), firebaseService) }

    override fun onCreate() {
        super.onCreate()
        // Memastikan Firebase menyala saat aplikasi start
        FirebaseApp.initializeApp(this)
    }
}