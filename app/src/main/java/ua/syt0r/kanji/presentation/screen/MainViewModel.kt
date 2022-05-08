package ua.syt0r.kanji.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsContract
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val analyticsManager: AnalyticsContract.Manager,
    private val userPreferences: UserDataContract.PreferencesRepository
) : ViewModel(), MainContract.ViewModel {

    override var createPracticeConfiguration: CreatePracticeConfiguration? = null
    override var writingPracticeConfiguration: WritingPracticeConfiguration? = null

    override fun shouldShowAnalyticsConsentDialog(): Flow<Boolean> {
        return userPreferences.showAnalyticsDialog
    }

    override fun consentForAnalytics() {
        viewModelScope.launch {
            analyticsManager.setAnalyticsEnabled(true)
            userPreferences.setAnalyticsEnabled(true)
            userPreferences.setShouldShownAnalyticsDialog(false)
        }
    }

}