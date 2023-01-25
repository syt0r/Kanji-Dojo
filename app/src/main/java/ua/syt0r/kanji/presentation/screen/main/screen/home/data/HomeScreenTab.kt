package ua.syt0r.kanji.presentation.screen.main.screen.home.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ua.syt0r.kanji.R

enum class HomeScreenTab(
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int
) {

    PRACTICE_DASHBOARD(
        iconResId = R.drawable.ic_baseline_translate_24,
        titleResId = R.string.home_tab_practice_dashboard
    ),

    SEARCH(
        iconResId = R.drawable.ic_baseline_search_24,
        titleResId = R.string.home_tab_search
    ),

    SETTINGS(
        iconResId = R.drawable.ic_outline_settings_24,
        titleResId = R.string.home_tab_settings
    );

    companion object {
        val visibleTabs = listOf(PRACTICE_DASHBOARD, SEARCH, SETTINGS)
        val defaultTab = PRACTICE_DASHBOARD
    }

}