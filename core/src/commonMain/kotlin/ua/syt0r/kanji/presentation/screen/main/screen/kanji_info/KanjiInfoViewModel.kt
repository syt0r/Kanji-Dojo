package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState

class KanjiInfoViewModel(
    private val viewModelScope: CoroutineScope,
    private val loadDataUseCase: KanjiInfoScreenContract.LoadDataUseCase,
    private val analyticsManager: AnalyticsManager
) : KanjiInfoScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun loadCharacterInfo(character: String) {
        val currentState = state.value
        if (currentState is ScreenState.Loaded) return

        viewModelScope.launch {
            state.value = withContext(Dispatchers.IO) {
                loadDataUseCase.load(character)
            }
        }
    }

    override fun reportScreenShown(character: String) {
        analyticsManager.setScreen("kanji_info")
        analyticsManager.sendEvent("kanji_info_open") {
            put("character", character)
        }
    }

}