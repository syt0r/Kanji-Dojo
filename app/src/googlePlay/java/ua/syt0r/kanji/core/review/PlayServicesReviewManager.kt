package ua.syt0r.kanji.core.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.ReviewInfo
import kotlinx.coroutines.delay
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.asActivity
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private typealias AndroidReviewManager = com.google.android.play.core.review.ReviewManager

class PlayServicesReviewManager @Inject constructor(
    private val reviewManager: AndroidReviewManager,
    private val analyticsManager: AnalyticsManager
) : ReviewManager {

    @Composable
    override fun StartReview() {
        val activity = LocalContext.current.asActivity()!!
        val isShownState = rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            Logger.logMethod()
            if (isShownState.value) {
                Logger.d("review flow already started, ignoring")
                return@LaunchedEffect
            }

            val request = reviewManager.requestReviewFlow()
            val reviewInfo: ReviewInfo? = suspendCoroutine { continuation ->
                request.addOnCompleteListener {
                    Logger.d("on review request completed[$it] reviewInfo[${it.result}]")
                    continuation.resume(it.result)
                }
            }

            reviewInfo?.let {
                delay(2000)
                Logger.d("starting review")
                isShownState.value = true
                analyticsManager.sendEvent("starting_in_app_review")
                reviewManager.launchReview(activity, it)
            }
        }
    }

}