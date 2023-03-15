package ua.syt0r.kanji.presentation.screen.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import org.koin.android.ext.android.inject
import ua.syt0r.kanji.core.review.LocalReviewManager
import ua.syt0r.kanji.core.review.ReviewManager
import ua.syt0r.kanji.presentation.KanjiDojoApp

class GooglePlayMainActivity : AppCompatActivity() {

    private val reviewManager: ReviewManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalReviewManager provides reviewManager
            ) {
                KanjiDojoApp()
            }
        }
    }

}