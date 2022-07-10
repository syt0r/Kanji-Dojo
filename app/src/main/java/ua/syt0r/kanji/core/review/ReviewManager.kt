package ua.syt0r.kanji.core.review

import androidx.appcompat.app.AppCompatActivity

interface ReviewManager {
    fun setupReview()
    suspend fun startReview(activity: AppCompatActivity)
}