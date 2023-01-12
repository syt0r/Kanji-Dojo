package ua.syt0r.kanji.presentation.screen.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.analytics.LocalAnalyticsManager
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.FdroidWritingPracticeScreenContent
import javax.inject.Inject

@AndroidEntryPoint
class FdroidMainActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            AppTheme {

                Surface(color = MaterialTheme.colors.background) {

                    CompositionLocalProvider(
                        LocalAnalyticsManager provides analyticsManager,
                        FlexibleScreens.LocalWritingPracticeScreenContent provides
                                FdroidWritingPracticeScreenContent
                    ) {
                        MainScreen()
                    }

                }

            }

        }

    }

}