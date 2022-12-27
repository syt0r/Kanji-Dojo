package ua.syt0r.kanji.core.review

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.compositionLocalOf

interface ReviewManager {
    fun setupReview()
    suspend fun startReview(activity: AppCompatActivity)
}

val LocalReviewManager = compositionLocalOf<ReviewManager> {
    error("Review manager is not set")
}