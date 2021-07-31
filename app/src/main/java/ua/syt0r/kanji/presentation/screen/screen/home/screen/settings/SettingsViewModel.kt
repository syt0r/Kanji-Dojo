package ua.syt0r.kanji.presentation.screen.screen.home.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel(), SettingsScreenContract.ViewModel {

    override fun updateNotificationsEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setNotificationDisplayTime() {
        TODO("Not yet implemented")
    }

}