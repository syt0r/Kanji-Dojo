package ua.syt0r.kanji.core.review

import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.ReviewInfo
import ua.syt0r.kanji.core.logger.Logger
import javax.inject.Inject

private typealias AndroidReviewManager = com.google.android.play.core.review.ReviewManager

class PlayServicesReviewManager @Inject constructor(
    val reviewManager: AndroidReviewManager
) : ReviewManager {

    private var reviewInfo: ReviewInfo? = null

    override fun setupReview() {
        Logger.logMethod()
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener {
            if (it.isSuccessful) reviewInfo = it.result
            Logger.d("on review request completed[$it] reviewInfo[$reviewInfo]")
        }
    }

    override suspend fun startReview(activity: AppCompatActivity) {
        Logger.d("on review start reviewInfo[$reviewInfo]")
        reviewInfo?.let {
            reviewManager.launchReview(activity, it)
        }
    }

}