package ua.syt0r.kanji.core.review

import androidx.compose.runtime.Composable

interface AppReviewContract {

    interface ReviewEligibilityUseCase {
        suspend fun checkIsEligible(): Boolean
    }

    interface ReviewManager {
        @Composable
        fun AttemptReview()
    }

}
