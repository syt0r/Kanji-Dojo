package ua.syt0r.kanji.core.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.asActivity
import ua.syt0r.kanji.presentation.screen.MainActivity


val LocalReviewManager = compositionLocalOf<ReviewManager> {
    error("Review manager is not set")
}

@Composable
fun ReviewManager.SetupReview() {
    LaunchedEffect(Unit) { setupReview() }
}

@Composable
fun ReviewManager.StartReview(
    reviewDelay: Long = 2000,
    onReviewCompleted: () -> Unit = {}
) {
    val activity = LocalContext.current.asActivity() as MainActivity
    LaunchedEffect(Unit) {
        delay(reviewDelay)
        startReview(activity)
        onReviewCompleted()
    }
}