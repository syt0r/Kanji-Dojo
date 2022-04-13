package ua.syt0r.kanji.presentation.screen.screen.home.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ua.syt0r.kanji.R

enum class HomeScreenTab(
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int
) {

    DASHBOARD(
        iconResId = R.drawable.ic_outline_home_24,
        titleResId = R.string.home_dashboard_title
    ),

    WRITING(
        iconResId = R.drawable.ic_baseline_translate_24,
        titleResId = R.string.home_writing_dashboard_title
    ),

    SETTINGS(
        iconResId = R.drawable.ic_outline_settings_24,
        titleResId = R.string.home_settings_title
    )

}