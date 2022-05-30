package ua.syt0r.kanji.core.in_app_review

import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import ua.syt0r.kanji.core.logger.Logger
import javax.inject.Inject

class InAppReviewManager @Inject constructor(
    val reviewManager: ReviewManager
) {

    private var reviewInfo: ReviewInfo? = null

    fun prepareForReview() {
        Logger.logMethod()
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener {
            if (it.isSuccessful) reviewInfo = it.result
            Logger.d("on review request completed[$it] reviewInfo[$reviewInfo]")
        }
    }

    suspend fun requestReview(activity: AppCompatActivity) {
        Logger.d("on review start reviewInfo[$reviewInfo]")
        reviewInfo?.let {
            reviewManager.launchReview(activity, it)
        }
    }

}