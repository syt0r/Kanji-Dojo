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
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PracticeGroup
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import javax.inject.Inject

@HiltViewModel
class PracticePreviewViewModel @Inject constructor(
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val fetchListUseCase: PracticePreviewScreenContract.FetchListUseCase,
    private val sortListUseCase: PracticePreviewScreenContract.SortListUseCase,
    private val createGroupsUseCase: PracticePreviewScreenContract.CreatePracticeGroupsUseCase
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private lateinit var sortConfiguration: SortConfiguration

    private var practiceId: Long = -1
    private lateinit var items: List<PracticeGroupItem>

    override fun loadPracticeInfo(practiceId: Long) {
        this.practiceId = practiceId
        viewModelScope.launch {
            state.value = ScreenState.Loading

            val characterList = withContext(Dispatchers.IO) {
                items = fetchListUseCase.fetch(practiceId)
                sortConfiguration = userPreferencesRepository.getSortConfiguration()
                    ?: SortConfiguration.default
                sortListUseCase.sort(sortConfiguration, items)
                    .let { createGroupsUseCase.create(it) }
            }

            state.value = ScreenState.Loaded(
                sortConfiguration = sortConfiguration,
                groups = characterList
            )
        }
    }

    override fun applySortConfig(configuration: SortConfiguration) {
        if (sortConfiguration == configuration) return

        val currentState = state.value
        if (currentState !is ScreenState.Loaded) return

        sortConfiguration = configuration

        viewModelScope.launch {
            state.value = ScreenState.Loading

            withContext(Dispatchers.IO) {

                userPreferencesRepository.setSortConfiguration(configuration)

                state.value = ScreenState.Loaded(
                    sortConfiguration = configuration,
                    groups = currentState.groups.flatMap { it.items }
                        .let { sortListUseCase.sort(configuration, it) }
                        .let { createGroupsUseCase.create(it) }
                )

            }
        }

    }

    override fun getPracticeConfiguration(
        practiceGroup: PracticeGroup,
        practiceConfiguration: PracticeConfiguration
    ): WritingPracticeConfiguration {
        return WritingPracticeConfiguration(
            practiceId = practiceId,
            characterList = practiceGroup.items
                .map { it.character }
                .let { if (practiceConfiguration.shuffle) it.shuffled() else it },
            isStudyMode = practiceConfiguration.isStudyMode
        )
    }

}