package ua.syt0r.kanji.core.analytics

import android.content.Context
import android.os.Bundle
import org.matomo.sdk.Matomo
import org.matomo.sdk.Tracker
import org.matomo.sdk.TrackerBuilder
import org.matomo.sdk.extra.TrackHelper

class MatomoAnalyticsManager constructor(
    isByDefaultEnabled: Boolean,
    private val context: Context
) : AnalyticsManager {

    companion object {
        private const val MATOMO_SERVER_URL = "192.168.0.103:8080"
        private const val MATOMO_KANJI_DOJO_SITE_ID = 1
        private const val MATOMO_EVENTS_CATEGORY = "default"
    }

    private var tracker: Tracker? = null

    init {
        setAnalyticsEnabled(isByDefaultEnabled)
    }

    @Synchronized
    override fun setAnalyticsEnabled(enabled: Boolean) {
        if (enabled) {
            tracker = TrackerBuilder
                .createDefault(MATOMO_SERVER_URL, MATOMO_KANJI_DOJO_SITE_ID)
                .build(Matomo.getInstance(context))
            TrackHelper.track().download().with(tracker)
        } else {
            tracker = null
        }
    }

    override fun setScreen(screenName: String) {
        tracker?.let { TrackHelper.track().screen(screenName).with(it) }
    }

    override fun sendEvent(eventName: String, parametersBuilder: Bundle.() -> Unit) {
        tracker?.let {
            TrackHelper.track()
                .apply {
                    val bundle = Bundle().apply(parametersBuilder)
                    bundle.keySet().forEachIndexed { index, key ->
                        dimension(index, bundle.getString(key))
                    }
                }
                .event(MATOMO_EVENTS_CATEGORY, eventName)
                .with(it)
        }
    }

}
