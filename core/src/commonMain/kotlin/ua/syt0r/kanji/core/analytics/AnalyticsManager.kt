package ua.syt0r.kanji.core.analytics

import androidx.annotation.Size

interface AnalyticsManager {

    fun setAnalyticsEnabled(enabled: Boolean)

    fun setScreen(screenName: String)

    fun sendEvent(
        @Size(min = 1L, max = 40L) eventName: String,
        parametersBuilder: MutableMap<String, Any>.() -> Unit = {}
    )

}
