package com.klmpk5.daycare_admin.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_admin.data.remote.model.ChildRemoteDto
import com.klmpk5.daycare_admin.data.remote.model.DailyScoreRemoteDto
import com.klmpk5.daycare_admin.data.remote.model.UserRemoteDto
import com.klmpk5.daycare_admin.data.remote.model.WeeklyPlanRemoteDto
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    // ==========================================
    // 1. CHILD OPERATIONS (ADMIN ACCESS)
    // ==========================================

    // Admin menarik SEMUA data anak
    suspend fun getAllChildren(): List<ChildRemoteDto> {
        val snapshot = db.collection("children")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val child = doc.toObject(ChildRemoteDto::class.java)
            child?.childId = doc.id
            child
        }
    }

    // Tambah Anak (Admin Only)
    suspend fun addChild(child: ChildRemoteDto) {
        val docRef = if (child.childId.isEmpty()) {
            db.collection("children").document()
        } else {
            db.collection("children").document(child.childId)
        }

        child.childId = docRef.id

        docRef.set(child).await()
    }

    // Update Anak (Admin Only)
    suspend fun updateChild(child: ChildRemoteDto) {
        if (child.childId.isEmpty()) return

        db.collection("children")
            .document(child.childId)
            .set(child)
            .await()
    }

    // Soft delete anak, bukan hapus permanen
    suspend fun softDeleteChild(childId: String) {
        if (childId.isEmpty()) return

        db.collection("children")
            .document(childId)
            .update(
                mapOf(
                    "active" to false,
                    "isActive" to false,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    // Kalau benar-benar mau hapus permanen dari Firestore
    suspend fun deleteChildPermanently(childId: String) {
        if (childId.isEmpty()) return

        db.collection("children")
            .document(childId)
            .delete()
            .await()
    }

    // ==========================================
    // 2. WEEKLY PLAN OPERATIONS (ADMIN ACCESS)
    // ==========================================

    suspend fun getAllWeeklyPlans(): List<WeeklyPlanRemoteDto> {
        val snapshot = db.collection("weekly_plans")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val plan = doc.toObject(WeeklyPlanRemoteDto::class.java)
            plan?.planId = doc.id
            plan
        }
    }

    suspend fun addWeeklyPlan(plan: WeeklyPlanRemoteDto) {
        val docRef = if (plan.planId.isEmpty()) {
            db.collection("weekly_plans").document()
        } else {
            db.collection("weekly_plans").document(plan.planId)
        }

        plan.planId = docRef.id

        docRef.set(plan).await()
    }

    // ==========================================
    // 3. DAILY SCORE OPERATIONS (ADMIN ACCESS)
    // ==========================================

    suspend fun getScoresByChild(childId: String): List<DailyScoreRemoteDto> {
        val snapshot = db.collection("daily_scores")
            .whereEqualTo("childId", childId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val score = doc.toObject(DailyScoreRemoteDto::class.java)
            score?.scoreId = doc.id
            score
        }
    }

    suspend fun addScore(score: DailyScoreRemoteDto) {
        val docRef = if (score.scoreId.isEmpty()) {
            db.collection("daily_scores").document()
        } else {
            db.collection("daily_scores").document(score.scoreId)
        }

        score.scoreId = docRef.id

        docRef.set(score).await()
    }

    // ==========================================
    // 4. USER OPERATIONS (AUTH & ROLE)
    // ==========================================

    suspend fun getUserRole(uid: String): String? {
        return try {
            val doc = db.collection("users")
                .document(uid)
                .get()
                .await()

            val user = doc.toObject(UserRemoteDto::class.java)
            user?.role

        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(
        uid: String,
        email: String,
        role: String
    ) {
        try {
            val userDto = UserRemoteDto(
                uid = uid,
                email = email,
                role = role
            )

            db.collection("users")
                .document(uid)
                .set(userDto)
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveUserProfile(uid: String, email: String, role: String) {
        try {
            val userDto = UserRemoteDto(
                uid = uid,
                email = email,
                role = role
            )
            // Menyimpan ke koleksi "users" dengan ID dokumen = UID dari Auth
            db.collection("users").document(uid).set(userDto).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}