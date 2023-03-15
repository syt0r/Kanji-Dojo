package ua.syt0r.kanji.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import ua.syt0r.kanji.core.logger.Logger

class FirebaseAnalyticsManager(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsManager {

    override fun setAnalyticsEnabled(enabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
    }

    override fun setScreen(screenName: String) {
        Logger.d("screenName[$screenName]")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }

    override fun sendEvent(
        eventName: String,
        parametersBuilder: MutableMap<String, Any>.() -> Unit
    ) {
        Logger.d("eventName[$eventName]")
        val parameters = mutableMapOf<String, Any>().apply { parametersBuilder() }
        firebaseAnalytics.logEvent(
            eventName,
            Bundle().apply {
                parameters.forEach { (key, value) -> putString(key, value.toString()) }
            }
        )
    }

}
