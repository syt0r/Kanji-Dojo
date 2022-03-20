package ua.syt0r.kanji.presentation.screen.screen.home.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.screen.screen.home.HomeScreenContract

enum class HomeScreenTab(
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int,
    val stylizedTitle: String
) {

    DASHBOARD(
        iconResId = R.drawable.ic_outline_home_24,
        titleResId = R.string.home_dashboard_title,
        stylizedTitle = "ホーム",
    ),

    WRITING(
        iconResId = R.drawable.ic_outline_edit_24,
        titleResId = R.string.home_writing_dashboard_title,
        stylizedTitle = "ホーム",
    ),

    SETTINGS(
        iconResId = R.drawable.ic_outline_settings_24,
        titleResId = R.string.home_settings_title,
        stylizedTitle = "設定",
    )

}