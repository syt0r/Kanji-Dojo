package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.*

interface PracticePreviewScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun updateScreenData(practiceId: Long)
        fun updateConfiguration(configuration: PracticePreviewScreenConfiguration)

        fun toggleMultiSelectMode()
        fun toggleSelectionForGroup(group: PracticeGroup)
        fun selectAll()
        fun deselectAll()

        fun getPracticeConfiguration(
            practiceGroup: PracticeGroup,
            practiceConfiguration: PracticeConfiguration
        ): PracticeScreenConfiguration

        fun getPracticeConfiguration(
            configuration: MultiselectPracticeConfiguration
        ): PracticeScreenConfiguration

        fun reportScreenShown()

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val title: String,
            val configuration: PracticePreviewScreenConfiguration,
            val items: List<PracticeGroupItem>,
            val groups: List<PracticeGroup>,
            val isMultiselectEnabled: Boolean,
            val selectedGroupIndexes: Set<Int>
        ) : ScreenState()

    }


    interface FetchGroupItemsUseCase {
        suspend fun fetch(practiceId: Long): List<PracticeGroupItem>
    }

    interface SortGroupItemsUseCase {
        fun sort(
            items: List<PracticeGroupItem>,
            sortOption: SortOption,
            isDescending: Boolean
        ): List<PracticeGroupItem>
    }

    interface FilterGroupItemsUseCase {
        fun filter(
            items: List<PracticeGroupItem>,
            practiceType: PracticeType,
            filterOption: FilterOption
        ): List<PracticeGroupItem>
    }

    interface CreatePracticeGroupsUseCase {
        fun create(items: List<PracticeGroupItem>, type: PracticeType): List<PracticeGroup>
    }

    interface GetPracticeSummary {
        suspend fun getSummary(character: String, type: PracticeType): PracticeSummary
    }

    interface ReloadDataUseCase {
        suspend fun load(practiceId: Long, previousState: ScreenState.Loaded?): ScreenState.Loaded
    }

}
