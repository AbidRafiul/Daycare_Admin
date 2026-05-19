package com.klmpk5.daycare_admin.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.klmpk5.daycare_admin.data.remote.model.AttendanceRemoteDto
import com.klmpk5.daycare_admin.data.remote.model.ChildRemoteDto
import com.klmpk5.daycare_admin.data.remote.model.DailyScoreRemoteDto
import com.klmpk5.daycare_admin.data.remote.model.UserRemoteDto
import com.klmpk5.daycare_admin.data.remote.model.WeeklyPlanRemoteDto
// FITUR BARU: Import untuk Chat
import com.klmpk5.daycare_admin.data.remote.model.ChatMessageRemoteDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    // ==========================================
    // 0. PRESENSI (ADMIN ACCESS)
    // ==========================================
    suspend fun addOrUpdateAttendance(attendance: AttendanceRemoteDto) {
        db.collection("attendance")
            .document(attendance.attendanceId)
            .set(attendance)
            .await()
    }

    suspend fun getAttendanceByDate(date: String): List<AttendanceRemoteDto> {
        return db.collection("attendance")
            .whereEqualTo("date", date)
            .get()
            .await()
            .toObjects(AttendanceRemoteDto::class.java)
    }

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

        val childWithParentEmail = child.copy(
            childId = docRef.id,
            parentEmail = child.parentEmail
                ?.trim()
                ?.lowercase()
                ?.ifBlank { null }
        )

        docRef.set(childWithParentEmail).await()
    }

    // Update Anak (Admin Only)
    suspend fun updateChild(child: ChildRemoteDto) {
        if (child.childId.isEmpty()) return

        val childWithParentEmail = child.copy(
            parentEmail = child.parentEmail
                ?.trim()
                ?.lowercase()
                ?.ifBlank { null }
        )

        db.collection("children")
            .document(child.childId)
            .set(childWithParentEmail)
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

    suspend fun getUserProfile(uid: String): UserRemoteDto? {
        return try {
            val doc = db.collection("users")
                .document(uid)
                .get()
                .await()

            val user = doc.toObject(UserRemoteDto::class.java)
            user?.uid = doc.id
            user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(
        uid: String,
        email: String,
        role: String,
        fullName: String = "",
        description: String = "",
        updatedAt: Long = System.currentTimeMillis()
    ) {
        try {
            val userDto = UserRemoteDto(
                uid = uid,
                email = email,
                role = role,
                fullName = fullName,
                description = description,
                updatedAt = updatedAt
            )

            db.collection("users")
                .document(uid)
                .set(userDto, SetOptions.merge())
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateUserProfile(
        uid: String,
        fullName: String,
        email: String,
        description: String,
        role: String = "admin",
        updatedAt: Long = System.currentTimeMillis()
    ) {
        db.collection("users")
            .document(uid)
            .set(
                mapOf(
                    "uid" to uid,
                    "fullName" to fullName,
                    "email" to email,
                    "role" to role,
                    "description" to description,
                    "updatedAt" to updatedAt
                ),
                SetOptions.merge()
            )
            .await()
    }

    // ==========================================
    // 5. GLOBAL CHAT OPERATIONS (REAL-TIME)
    // ==========================================

    fun getGlobalChats(): Flow<List<ChatMessageRemoteDto>> = callbackFlow {
        val listener = db.collection("global_chats")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatMessageRemoteDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(messages)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendChatMessage(chatMessage: ChatMessageRemoteDto) {
        db.collection("global_chats")
            .add(chatMessage)
            .await()
    }
}
