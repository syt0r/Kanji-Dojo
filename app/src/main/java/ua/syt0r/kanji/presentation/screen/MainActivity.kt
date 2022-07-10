package ua.syt0r.kanji.presentation.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.review.LocalReviewManager
import ua.syt0r.kanji.core.review.ReviewManager
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var reviewManager: ReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            AppTheme {

                Surface(color = MaterialTheme.colors.background) {

                    CompositionLocalProvider(LocalReviewManager provides reviewManager) {
                        MainScreen()
                    }

                }

            }

        }

    }

}
