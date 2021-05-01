package ua.syt0r.kanji.ui.screen.screen.home

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import ua.syt0r.kanji.R

interface HomeScreenContract {

    interface ViewModel {
        val currentScreen: LiveData<Screen>
        fun selectScreen(screen: Screen)
    }

    enum class Screen(
        @StringRes val textResId: Int,
        val stylizedText: String
    ) {

        GENERAL_DASHBOARD(R.string.home_general_dashboard_title, "情報"),
        WRITING_DASHBOARD(R.string.home_writing_dashboard_title, "書く"),
        SEARCH(R.string.home_search_title, "探す"),
        SETTINGS(R.string.home_settings_title, "設定");

        companion object {
            val DEFAULT = GENERAL_DASHBOARD
        }

    }

}