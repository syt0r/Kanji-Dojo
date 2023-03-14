package ua.syt0r.kanji.core.analytics

class PrintAnalyticsManager : AnalyticsManager {

    private var isEnabled = true

    override fun setAnalyticsEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    override fun setScreen(screenName: String) {
        println("Analytics Screen: $screenName")
    }

    override fun sendEvent(
        eventName: String,
        parametersBuilder: MutableMap<String, Any>.() -> Unit
    ) {
        val argumentsMessage = mutableMapOf<String, Any>().apply { parametersBuilder() }
            .toList()
            .joinToString { (key, value) -> "$key=$value" }
        println("Analytics Event: $eventName, argument=$argumentsMessage")
    }
}