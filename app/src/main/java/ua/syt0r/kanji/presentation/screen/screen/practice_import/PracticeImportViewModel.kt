package ua.syt0r.kanji.presentation.screen.screen.practice_import

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_import.PracticeImportScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.jlptImportPracticeCategory
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.kanaImportPracticeCategory
import javax.inject.Inject


@HiltViewModel
class PracticeImportViewModel @Inject constructor(
    private val kanjiDataRepository: KanjiDataContract.Repository
) : ViewModel(), PracticeImportScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    init {
        state.value = ScreenState.Loaded(
            listOf(kanaImportPracticeCategory, jlptImportPracticeCategory)
        )
    }

}