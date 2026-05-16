package com.klmpk5.daycare_admin

import android.app.Application
import androidx.room.Room
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.klmpk5.daycare_admin.data.local.db.DaycareDatabase
import com.klmpk5.daycare_admin.data.remote.CloudinaryService
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.repository.AttendanceRepository
import com.klmpk5.daycare_admin.repository.ChildRepository
import com.klmpk5.daycare_admin.repository.ScoreRepository
import com.klmpk5.daycare_admin.repository.UserRepository
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
    val attendanceRepository by lazy {
        AttendanceRepository(database.attendanceDao(), firebaseService)
    }

    val weeklyPlanRepository by lazy {
        WeeklyPlanRepository(database.weeklyPlanDao(), firebaseService)
    }

    val scoreRepository by lazy {
        ScoreRepository(database.scoreDao(), firebaseService, cloudinaryService)
    }

    val userRepository by lazy {
        UserRepository(database.userDao(), firebaseService)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Konfigurasi Cloudinary.
        val config = mapOf("cloud_name" to "dl7vqliwp")
        MediaManager.init(this, config)
    }
}
