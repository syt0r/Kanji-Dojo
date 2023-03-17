package ua.syt0r.kanji.core.review

import android.app.Activity
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private typealias AndroidReviewManager = com.google.android.play.core.review.ReviewManager

class PlayServicesReviewManager(
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

            kotlin.runCatching {
                performReview(
                    activity = activity,
                    beforeReviewUIShown = { isShownState.value = true }
                )
            }.getOrElse {
                isShownState.value = true
                analyticsManager.sendEvent("in_app_review_error") {
                    put("message", it.toString())
                }
            }

        }
    }

    private suspend fun performReview(
        activity: Activity,
        beforeReviewUIShown: () -> Unit
    ) {
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
            analyticsManager.sendEvent("starting_in_app_review")
            beforeReviewUIShown()
            reviewManager.launchReview(activity, it)
        }
    }

}