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

        GENERAL_DASHBOARD(R.string.home_general_dashboard_title, "全"),
        WRITING_DASHBOARD(R.string.home_writing_dashboard_title, "書"),
        SEARCH(R.string.home_search_title, "字"),
        SETTINGS(R.string.home_settings_title, "他");

        companion object {
            val DEFAULT = GENERAL_DASHBOARD
        }

    }

}