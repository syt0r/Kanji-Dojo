package ua.syt0r.kanji.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import ua.syt0r.kanji.core.logger.Logger

class AnalyticsManager(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsContract.Manager {

    override fun setAnalyticsEnabled(enabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
    }

    override fun setScreen(screenName: String) {
        Logger.d("screenName[$screenName]")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }

    override fun sendEvent(eventName: String, parametersBuilder: Bundle.() -> Unit) {
        Logger.d("eventName[$eventName]")
        firebaseAnalytics.logEvent(
            eventName,
            Bundle().apply { parametersBuilder() }
        )
    }

}
