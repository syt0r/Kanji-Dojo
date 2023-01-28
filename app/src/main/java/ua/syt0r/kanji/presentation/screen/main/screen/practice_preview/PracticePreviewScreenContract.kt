package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

interface PracticePreviewScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun loadPracticeInfo(practiceId: Long)
        fun applySortConfig(configuration: SortConfiguration)

        fun toggleMultiSelectMode()
        fun toggleSelectionForGroup(group: PracticeGroup)

        fun getPracticeConfiguration(
            practiceGroup: PracticeGroup,
            practiceConfiguration: PracticeConfiguration
        ): WritingPracticeConfiguration

        fun getPracticeConfiguration(
            configuration: MultiselectPracticeConfiguration
        ): WritingPracticeConfiguration

        fun reportScreenShown()

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val title: String,
            val sortConfiguration: SortConfiguration,
            val groups: List<PracticeGroup>,
            val isMultiselectEnabled: Boolean,
            val selectedGroupIndexes: Set<Int>
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

    interface PracticePreviewCharacterStateUseCase {
        suspend fun calculateState(character: String): CharacterReviewState
    }

}
