package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PreviewCharacterData
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionConfiguration
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PracticePreviewViewModel @Inject constructor(
    private val usedDataRepository: UserDataContract.PracticeRepository
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun loadPracticeInfo(practiceId: Long) {
        viewModelScope.launch {
            state.value = ScreenState.Loading
            val kanjiList = withContext(Dispatchers.IO) {
                usedDataRepository.getKanjiForPracticeSet(practiceId)
                    .map { PreviewCharacterData(it, Random.nextBoolean()) }
            }
            state.value = ScreenState.Loaded(
                practiceId = practiceId,
                selectionConfig = SelectionConfiguration.default,
                characterData = kanjiList,
                selectedCharacters = listOf()
            )
        }
    }

    override fun submitSelectionConfig(configuration: SelectionConfiguration) {
        state.value = (state.value as ScreenState.Loaded).run {
            copy()
        }
    }

}