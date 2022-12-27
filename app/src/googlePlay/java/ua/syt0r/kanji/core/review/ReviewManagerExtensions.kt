package ua.syt0r.kanji.core.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.asActivity


@Composable
fun ReviewManager.SetupReview() {
    LaunchedEffect(Unit) { setupReview() }
}

@Composable
fun ReviewManager.StartReview(
    reviewDelay: Long = 2000,
    onReviewCompleted: () -> Unit = {}
) {
    val activity = LocalContext.current.asActivity()!!
    LaunchedEffect(Unit) {
        delay(reviewDelay)
        startReview(activity)
        onReviewCompleted()
    }
}