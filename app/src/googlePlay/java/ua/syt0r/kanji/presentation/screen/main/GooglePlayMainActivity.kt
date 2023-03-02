package ua.syt0r.kanji.presentation.screen.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.analytics.LocalAnalyticsManager
import ua.syt0r.kanji.core.review.LocalReviewManager
import ua.syt0r.kanji.core.review.ReviewManager
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.GooglePlayWritingPracticeScreenContent
import javax.inject.Inject

@AndroidEntryPoint
class GooglePlayMainActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var reviewManager: ReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            AppTheme {

                Surface {

                    CompositionLocalProvider(
                        LocalReviewManager provides reviewManager,
                        LocalAnalyticsManager provides analyticsManager,
                        FlexibleScreens.LocalWritingPracticeScreenContent provides
                                GooglePlayWritingPracticeScreenContent
                    ) {
                        MainScreen()
                    }

                }

            }

        }

    }

}