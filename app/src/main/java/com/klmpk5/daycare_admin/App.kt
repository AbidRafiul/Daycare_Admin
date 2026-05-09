package com.klmpk5.daycare_admin

import android.app.Application
import androidx.room.Room
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.klmpk5.daycare_admin.data.local.db.DaycareDatabase
import com.klmpk5.daycare_admin.data.remote.CloudinaryService
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.repository.ChildRepository
import com.klmpk5.daycare_admin.repository.ScoreRepository
import com.klmpk5.daycare_admin.repository.WeeklyPlanRepository

class App : Application() {
    private val firebaseService by lazy { FirebaseService() }
    private val cloudinaryService by lazy { CloudinaryService() }

    val database by lazy {
        Room.databaseBuilder(
            this,
            DaycareDatabase::class.java,
            "daycare_admin_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val childRepository by lazy {
        ChildRepository(database.childDao(), firebaseService, cloudinaryService)
    }

    val weeklyPlanRepository by lazy {
        WeeklyPlanRepository(database.weeklyPlanDao(), firebaseService)
    }

    val scoreRepository by lazy {
        ScoreRepository(database.scoreDao(), firebaseService)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Konfigurasi Cloudinary.
        // GANTI "NAMA_CLOUD_KAMU" dengan Cloud Name dari dashboard Cloudinary kamu.
        val config = mapOf("cloud_name" to "NAMA_CLOUD_KAMU")
        MediaManager.init(this, config)
    }
}