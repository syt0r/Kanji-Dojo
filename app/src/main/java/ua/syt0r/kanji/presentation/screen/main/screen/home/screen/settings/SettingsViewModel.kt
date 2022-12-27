package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), SettingsScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    init {
        viewModelScope.launch {
            state.value = ScreenState.Loaded(
                analyticsEnabled = userPreferencesRepository.analyticsEnabled.first()
            )
        }
    }

    override fun updateAnalyticsEnabled(enabled: Boolean) {
        Logger.logMethod()
        viewModelScope.launch {
            analyticsManager.setAnalyticsEnabled(enabled)
            analyticsManager.sendEvent("analytics_toggled") {
                putBoolean("analytics_enabled", enabled)
            }
            userPreferencesRepository.setAnalyticsEnabled(enabled)
            state.value = ScreenState.Loaded(analyticsEnabled = enabled)
        }
    }

}