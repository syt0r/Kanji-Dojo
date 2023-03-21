package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState

class SettingsViewModel(
    private val viewModelScope: CoroutineScope,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager
) : SettingsScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun refresh() {
        viewModelScope.launch {
            state.value = ScreenState.Loaded(
                analyticsEnabled = userPreferencesRepository.getAnalyticsEnabled(),
                noTranslationLayoutEnabled = userPreferencesRepository.getNoTranslationsLayoutEnabled(),
                leftHandedModeEnabled = userPreferencesRepository.getLeftHandedModeEnabled()
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

    override fun updateLeftHandedMode(enabled: Boolean) {
        val currentState = state.value as ScreenState.Loaded
        viewModelScope.launch {

            userPreferencesRepository.setLeftHandedModeEnabled(enabled)
            state.value = currentState.copy(leftHandedModeEnabled = enabled)

            analyticsManager.sendEvent("left_handed_mode_toggled") {
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