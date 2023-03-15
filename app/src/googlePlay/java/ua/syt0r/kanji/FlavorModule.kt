package ua.syt0r.kanji

import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.analytics.FirebaseAnalyticsManager
import ua.syt0r.kanji.core.review.PlayServicesReviewManager
import ua.syt0r.kanji.core.user_data.AndroidUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.GooglePlayWritingPracticeScreenContent
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract

val flavorModule = module {

    single<WritingPracticeScreenContract.Content> { GooglePlayWritingPracticeScreenContent }

    single<AnalyticsManager> { FirebaseAnalyticsManager(firebaseAnalytics = Firebase.analytics) }

    single<ReviewManager> { ReviewManagerFactory.create(androidApplication()) }
    single<ua.syt0r.kanji.core.review.ReviewManager> {
        PlayServicesReviewManager(
            reviewManager = get(),
            analyticsManager = get()
        )
    }

    single<UserPreferencesRepository> {
        AndroidUserPreferencesRepository(
            context = androidContext(),
            defaultAnalyticsEnabled = true,
            defaultAnalyticsSuggestionEnabled = false
        )
    }

}