package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.items

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsSwitchRow

/**
 * Exposes the existing kanaAutoPlay preference in Settings (previously only reachable via the
 * small toggle inside the practice screen itself). Controls both the existing reveal-time
 * auto-play and the answer-submit playback added in LetterPracticeViewModel.submitAnswer.
 */
class PronunciationAutoPlaySettingItem(
    private val practicePreferences: PreferencesContract.PracticePreferences
) : SettingsScreenContract.ConfigurableListItem {

    private lateinit var enabled: MutableState<Boolean>

    override suspend fun prepare(coroutineScope: CoroutineScope) {
        enabled = mutableStateOf(practicePreferences.kanaAutoPlay.get())
        snapshotFlow { enabled.value }
            .drop(1)
            .onEach { practicePreferences.kanaAutoPlay.set(it) }
            .launchIn(coroutineScope)
    }

    @Composable
    override fun content(mainNavigationState: MainNavigationState) {
        SettingsSwitchRow(
            title = "Auto-play pronunciation",
            message = "Automatically play kana/kanji audio during writing practice",
            isEnabled = enabled.value,
            onToggled = { enabled.value = !enabled.value }
        )
    }

}
