package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), SettingsScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun refresh() {
        viewModelScope.launch {
            state.value = ScreenState.Loaded(
                analyticsEnabled = userPreferencesRepository.getAnalyticsEnabled(),
                noTranslationLayoutEnabled = userPreferencesRepository.getNoTranslationsLayoutEnabled()
            )
        }
    }

    override fun updateNoTranslationLayout(enabled: Boolean) {
        val currentState = state.value as ScreenState.Loaded
        viewModelScope.launch {

            userPreferencesRepository.setNoTranslationsLayoutEnabled(enabled)
            state.value = currentState.copy(noTranslationLayoutEnabled = enabled)

            analyticsManager.sendEvent("no_translations_layout_toggled") {
                put("enabled", enabled)
            }

        }
    }

    override fun updateAnalyticsEnabled(enabled: Boolean) {
        val currentState = state.value as ScreenState.Loaded
        viewModelScope.launch {

            userPreferencesRepository.setAnalyticsEnabled(enabled)
            state.value = currentState.copy(analyticsEnabled = enabled)

            analyticsManager.setAnalyticsEnabled(enabled)
            analyticsManager.sendEvent("analytics_toggled") {
                put("analytics_enabled", enabled)
            }

        }
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("settings")
    }

}