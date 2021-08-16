package ua.syt0r.kanji.presentation.screen.screen.home.data

import androidx.annotation.StringRes
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.screen.screen.home.HomeScreenContract

enum class HomeScreenTab(
    @StringRes val titleResId: Int,
    val stylizedTitle: String,
    val navigate: HomeScreenContract.Navigation.() -> Unit
) {

    DASHBOARD(
        titleResId = R.string.home_dashboard_title,
        stylizedTitle = "ホーム",
        navigate = { navigateToDashboard() }
    ),

    SETTINGS(
        titleResId = R.string.home_settings_title,
        stylizedTitle = "設定",
        navigate = { navigateToSettings() }
    )

}

// TODO Old style
// enum class Screen(
//    @StringRes val textResId: Int,
//    val stylizedText: String
//) {
//
//    GENERAL_DASHBOARD(R.string.home_dashboard_title, "情報"),
//    WRITING_DASHBOARD(R.string.home_writing_dashboard_title, "書く"),
//    SEARCH(R.string.home_search_title, "探す"),
//    SETTINGS(R.string.home_settings_title, "設定");
//
//    companion object {
//        val DEFAULT = GENERAL_DASHBOARD
//    }
//
//}