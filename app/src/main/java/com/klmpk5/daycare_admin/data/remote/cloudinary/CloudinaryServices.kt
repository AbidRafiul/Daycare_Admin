package com.klmpk5.daycare_admin.data.remote

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CloudinaryService {
    suspend fun uploadImage(imageUri: Uri): String? = suspendCancellableCoroutine { continuation ->
        // Ganti "daycare_preset" dengan nama Upload preset Unsigned yang kamu buat di web Cloudinary
        MediaManager.get().upload(imageUri)
            .unsigned("daycare_preset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val secureUrl = resultData["secure_url"] as? String
                    if (continuation.isActive) {
                        continuation.resume(secureUrl)
                    }
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    error.description?.let { println("Cloudinary Error: $it") }
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            }).dispatch()
    }
}