package ua.syt0r.kanji.core.analytics

interface AnalyticsManager {

    fun setAnalyticsEnabled(enabled: Boolean)

    fun setScreen(screenName: String)

    fun sendEvent(
        eventName: String,
        parametersBuilder: MutableMap<String, Any>.() -> Unit = {}
    )

}
