package ua.syt0r.kanji.core.analytics

import android.os.Bundle
import androidx.annotation.Size

interface AnalyticsContract {

    interface Manager {

        fun setAnalyticsEnabled(enabled: Boolean)
        fun setScreen(screenName: String)

        fun sendEvent(
            @Size(min = 1L, max = 40L) eventName: String,
            parametersBuilder: Bundle.() -> Unit = {}
        )

    }

}