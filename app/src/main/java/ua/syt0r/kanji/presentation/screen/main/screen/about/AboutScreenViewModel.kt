package ua.syt0r.kanji.presentation.screen.main.screen.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import javax.inject.Inject

@HiltViewModel
class AboutScreenViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager
) : ViewModel(), AboutScreenContract.ViewModel {

    override fun reportScreenShown() {
        analyticsManager.setScreen("about")
    }

    override fun reportUrlClick(url: String) {
        analyticsManager.sendEvent("about_url_click") { put("url", url) }
    }
}