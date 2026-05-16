package com.klmpk5.daycare_admin.repository

import android.net.Uri
import com.klmpk5.daycare_admin.data.local.dao.ScoreDao
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.data.remote.CloudinaryService
import com.klmpk5.daycare_admin.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_admin.data.remote.model.DailyScoreRemoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ScoreRepository(
    private val scoreDao: ScoreDao,
    private val firebaseService: FirebaseService,
    private val cloudinaryService: CloudinaryService
) {
    // 1. Membaca nilai harian berdasarkan ID anak dari database lokal
    fun getScoresByChildLocal(childId: String): Flow<List<DailyScore>> {
        return scoreDao.getScoresByChildId(childId)
    }

    fun getScoresByDateLocal(date: String): Flow<List<DailyScore>> {
        return scoreDao.getScoresByDate(date)
    }

    // 2. Sinkronisasi nilai harian dari Firebase ke Room
    suspend fun syncScoresFromRemote(childId: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteScores = firebaseService.getScoresByChild(childId)

                // Fungsi toEntity() milik DailyScoreRemoteDto dipanggil di sini
                val localScores = remoteScores.map { it.toEntity() }

                localScores.forEach { score ->
                    scoreDao.insertScore(score)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 3. Menambahkan nilai harian baru (Khusus Admin/Guru)
    suspend fun addScore(scoreEntity: DailyScore, imageUri: Uri? = null) {
        withContext(Dispatchers.IO) {
            try {
                val uploadedImageUrl = imageUri?.let { uri ->
                    cloudinaryService.uploadImage(uri)
                }

                val scoreWithImage = scoreEntity.copy(
                    imageUrl = uploadedImageUrl ?: scoreEntity.imageUrl
                )

                // Simpan ke Room
                scoreDao.insertScore(scoreWithImage)

                // Konversi ke DTO
                val remoteDto = DailyScoreRemoteDto(
                    scoreId = scoreWithImage.scoreId,
                    childId = scoreWithImage.childId,
                    date = scoreWithImage.date,
                    activityName = scoreWithImage.activityName,
                    score = scoreWithImage.score,
                    notes = scoreWithImage.notes,
                    imageUrl = scoreWithImage.imageUrl
                )

                // Lempar ke Firebase
                firebaseService.addScore(remoteDto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
