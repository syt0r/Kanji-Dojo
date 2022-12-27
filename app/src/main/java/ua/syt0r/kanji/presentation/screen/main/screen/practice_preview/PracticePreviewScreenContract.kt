package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

interface PracticePreviewScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun loadPracticeInfo(practiceId: Long)
        fun applySortConfig(configuration: SortConfiguration)

        fun getPracticeConfiguration(
            practiceGroup: PracticeGroup,
            practiceConfiguration: PracticeConfiguration
        ): WritingPracticeConfiguration

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val sortConfiguration: SortConfiguration,
            val groups: List<PracticeGroup>
        ) : ScreenState()

    }


    interface FetchListUseCase {
        suspend fun fetch(practiceId: Long): List<PracticeGroupItem>
    }

    interface SortListUseCase {
        fun sort(
            sortConfiguration: SortConfiguration,
            characterList: List<PracticeGroupItem>
        ): List<PracticeGroupItem>
    }

    interface CreatePracticeGroupsUseCase {
        fun create(
            characterList: List<PracticeGroupItem>
        ): List<PracticeGroup>
    }

}
