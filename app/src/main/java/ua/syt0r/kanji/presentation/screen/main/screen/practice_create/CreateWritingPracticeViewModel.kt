package ua.syt0r.kanji.presentation.screen.main.screen.practice_create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.InputProcessingResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case.LoadPracticeDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case.ProcessInputUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case.SavePracticeUseCase
import javax.inject.Inject

@HiltViewModel
class CreateWritingPracticeViewModel @Inject constructor(
    private val loadPracticeDataUseCase: LoadPracticeDataUseCase,
    private val practiceRepository: UserDataContract.PracticeRepository,
    private val processInputUseCase: ProcessInputUseCase,
    private val savePracticeUseCase: SavePracticeUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), CreateWritingPracticeScreenContract.ViewModel {

    private lateinit var configuration: MainDestination.CreatePractice

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun initialize(configuration: MainDestination.CreatePractice) {
        if (!this::configuration.isInitialized) {
            this.configuration = configuration

            viewModelScope.launch {

                state.value = ScreenState.Loading

                val data = withContext(Dispatchers.IO) {
                    loadPracticeDataUseCase.load(configuration)
                }

                state.value = ScreenState.Loaded(
                    initialPracticeTitle = data.title,
                    characters = data.characters,
                    charactersPendingForRemoval = emptySet(),
                    currentDataAction = DataAction.Loaded
                )
            }

        }
    }

    override suspend fun submitUserInput(input: String): InputProcessingResult {
        val result: Deferred<InputProcessingResult> = viewModelScope.async {

            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.ProcessingInput)

            val (processingResult, updatedCharactersSet) = withContext(Dispatchers.IO) {
                val processingResult = processInputUseCase.processInput(input)
                processingResult to screenState.characters + processingResult.detectedCharacter
            }

            state.value = screenState.copy(
                characters = updatedCharactersSet,
                currentDataAction = DataAction.Loaded
            )

            processingResult
        }

        return result.await()
    }

    override fun remove(character: String) {
        val screenState = state.value as ScreenState.Loaded
        state.value = screenState.run {
            when (configuration) {
                is MainDestination.CreatePractice.EditExisting -> {
                    copy(charactersPendingForRemoval = charactersPendingForRemoval.plus(character))
                }
                else -> {
                    copy(characters = characters.minus(character))
                }
            }
        }
    }

    override fun cancelRemoval(character: String) {
        val screenState = state.value as ScreenState.Loaded
        state.value = screenState.run {
            copy(charactersPendingForRemoval = charactersPendingForRemoval.minus(character))
        }
    }

    override fun savePractice(title: String) {
        viewModelScope.launch {
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.Saving)

            withContext(Dispatchers.IO) {
                savePracticeUseCase.save(configuration, title, screenState)
                analyticsManager.sendEvent("removed_items") {
                    putInt("removed_items", screenState.charactersPendingForRemoval.size)
                }
            }

            state.value = screenState.copy(currentDataAction = DataAction.SaveCompleted)
        }
    }

    override fun deletePractice() {
        viewModelScope.launch {
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.Deleting)

            val practiceId =
                (configuration as MainDestination.CreatePractice.EditExisting).practiceId

            withContext(Dispatchers.IO) {
                practiceRepository.deletePractice(practiceId)
            }

            state.value = screenState.copy(currentDataAction = DataAction.DeleteCompleted)
        }
    }

    override fun reportScreenShown(configuration: MainDestination.CreatePractice) {
        analyticsManager.setScreen("practice_create")
        analyticsManager.sendEvent("writing_practice_configuration") {
            when (configuration) {
                is MainDestination.CreatePractice.Import -> {
                    putString("mode", "import")
                    putString("practice_title", configuration.title)
                }
                is MainDestination.CreatePractice.EditExisting -> {
                    putString("mode", "edit")
                }
                is MainDestination.CreatePractice.New -> {
                    putString("mode", "new")
                }
            }
        }
    }

}