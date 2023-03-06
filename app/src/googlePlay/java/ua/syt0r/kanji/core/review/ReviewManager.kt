package ua.syt0r.kanji.core.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

interface ReviewManager {
    @Composable
    fun StartReview()
}

val LocalReviewManager = compositionLocalOf<ReviewManager> {
    error("Review manager is not set")
}