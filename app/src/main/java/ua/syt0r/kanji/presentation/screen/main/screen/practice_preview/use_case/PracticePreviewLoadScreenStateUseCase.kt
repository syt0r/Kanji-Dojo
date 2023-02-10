package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.VisibilityConfiguration
import javax.inject.Inject

class PracticePreviewLoadScreenStateUseCase @Inject constructor(
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val practiceRepository: UserDataContract.PracticeRepository,
    private val fetchGroupItemsUseCase: PracticePreviewScreenContract.FetchGroupItemsUseCase,
    private val sortGroupItemsUseCase: PracticePreviewScreenContract.SortGroupItemsUseCase,
    private val createGroupsUseCase: PracticePreviewScreenContract.CreatePracticeGroupsUseCase,
) : PracticePreviewScreenContract.LoadScreenDataUseCase {

    override suspend fun load(
        practiceId: Long,
        previousState: ScreenState.Loaded?
    ): ScreenState.Loaded {
        val sortConfiguration = userPreferencesRepository.getSortConfiguration()
            ?: SortConfiguration()
        val visibilityConfiguration = previousState?.visibilityConfiguration
            ?: VisibilityConfiguration()

        val charactersList = fetchGroupItemsUseCase.fetch(practiceId)
            .let { sortGroupItemsUseCase.sort(sortConfiguration, it) }

        val allGroups = createGroupsUseCase.create(charactersList)
        val reviewOnlyGroups = createGroupsUseCase.create(
            charactersList.filter { it.reviewState == CharacterReviewState.NeedReview }
        )

        val selectedGroups = previousState?.selectedGroupIndexes
            ?.let { previouslySelectedGroups ->
                val groups = if (visibilityConfiguration.reviewOnlyGroups) reviewOnlyGroups
                else allGroups

                val currentlyAvailableGroupIndexes = groups.map { it.index }.toSet()
                previouslySelectedGroups.intersect(currentlyAvailableGroupIndexes)
            }
            ?: emptySet()


        return ScreenState.Loaded(
            title = practiceRepository.getPracticeInfo(practiceId).name,
            sortConfiguration = sortConfiguration,
            visibilityConfiguration = visibilityConfiguration,
            allGroups = allGroups,
            reviewOnlyGroups = reviewOnlyGroups,
            isMultiselectEnabled = previousState?.isMultiselectEnabled ?: false,
            selectedGroupIndexes = selectedGroups
        )
    }

}