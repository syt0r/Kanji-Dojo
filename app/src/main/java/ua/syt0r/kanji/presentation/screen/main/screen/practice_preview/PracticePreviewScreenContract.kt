package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

interface PracticePreviewScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun loadPracticeInfo(practiceId: Long)
        fun applySortConfig(configuration: SortConfiguration)
        fun applyVisibilityConfig(configuration: VisibilityConfiguration)

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
            val visibilityConfiguration: VisibilityConfiguration,
            val allGroups: List<PracticeGroup>,
            val reviewOnlyGroups: List<PracticeGroup>,
            val isMultiselectEnabled: Boolean,
            val selectedGroupIndexes: Set<Int>
        ) : ScreenState() {

            val groups = when (visibilityConfiguration.reviewOnlyGroups) {
                true -> reviewOnlyGroups
                false -> allGroups
            }

        }

    }


    interface FetchGroupItemsUseCase {
        suspend fun fetch(practiceId: Long): List<PracticeGroupItem>
    }

    interface SortGroupItemsUseCase {
        fun sort(
            sortConfiguration: SortConfiguration,
            groupItems: List<PracticeGroupItem>
        ): List<PracticeGroupItem>
    }

    interface CreatePracticeGroupsUseCase {
        fun create(groupItems: List<PracticeGroupItem>): List<PracticeGroup>
    }

    interface CalculateCharacterStateUseCase {
        suspend fun calculateState(character: String): CharacterReviewState
    }

    interface LoadScreenDataUseCase {
        suspend fun load(practiceId: Long, previousState: ScreenState.Loaded?): ScreenState.Loaded
    }

}
