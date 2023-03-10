package ua.syt0r.kanji.core

import org.koin.dsl.module
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.SqlDelightPracticeRepository

val coreModule = module {

    single<AnalyticsManager> {
        // TODO replace
        object : AnalyticsManager {
            override fun setAnalyticsEnabled(enabled: Boolean) {}

            override fun setScreen(screenName: String) {}

            override fun sendEvent(
                eventName: String,
                parametersBuilder: MutableMap<String, Any>.() -> Unit
            ) {
            }
        }
    }

    single<PracticeRepository> {
        SqlDelightPracticeRepository()
    }

}