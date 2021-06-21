package ua.syt0r.kanji.ui.screen.screen.home.screen.settings

interface SettingsScreenContract {

    interface ViewModel {
        fun updateNotificationsEnabled(enabled: Boolean)
        fun setNotificationDisplayTime()
    }

}