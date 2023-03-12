package ua.syt0r.kanji.presentation.screen.main.screen.practice_create

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.ValidationResult

class PracticeCreateViewModel(
    private val viewModelScope: CoroutineScope,
    private val loadDataUseCase: PracticeCreateScreenContract.LoadDataUseCase,
    private val validateCharactersUseCase: PracticeCreateScreenContract.ValidateCharactersUseCase,
    private val savePracticeUseCase: PracticeCreateScreenContract.SavePracticeUseCase,
    private val deletePracticeUseCase: PracticeCreateScreenContract.DeletePracticeUseCase,
    private val analyticsManager: AnalyticsManager
) : PracticeCreateScreenContract.ViewModel {

    private lateinit var configuration: MainDestination.CreatePractice

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun initialize(configuration: MainDestination.CreatePractice) {
        if (!this::configuration.isInitialized) {
            this.configuration = configuration

            viewModelScope.launch {

                state.value = ScreenState.Loading

                val data = withContext(Dispatchers.IO) {
                    loadDataUseCase.load(configuration)
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

    override suspend fun submitUserInput(input: String): ValidationResult {
        val result: Deferred<ValidationResult> = viewModelScope.async {

            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.ProcessingInput)

            val (processingResult, updatedCharactersSet) = withContext(Dispatchers.IO) {
                val processingResult = validateCharactersUseCase.processInput(input)
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
                    put("removed_items", screenState.charactersPendingForRemoval.size)
                }
            }

            state.value = screenState.copy(currentDataAction = DataAction.SaveCompleted)
        }
    }

    override fun deletePractice() {
        viewModelScope.launch {
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(currentDataAction = DataAction.Deleting)

            val practiceId = configuration
                .let { it as MainDestination.CreatePractice.EditExisting }
                .practiceId

            withContext(Dispatchers.IO) { deletePracticeUseCase.delete(practiceId) }

            state.value = screenState.copy(currentDataAction = DataAction.DeleteCompleted)
        }
    }

    override fun reportScreenShown(configuration: MainDestination.CreatePractice) {
        analyticsManager.setScreen("practice_create")
        analyticsManager.sendEvent("writing_practice_configuration") {
            when (configuration) {
                is MainDestination.CreatePractice.Import -> {
                    put("mode", "import")
                    put("practice_title", configuration.title)
                }
                is MainDestination.CreatePractice.EditExisting -> {
                    put("mode", "edit")
                }
                is MainDestination.CreatePractice.New -> {
                    put("mode", "new")
                }
            }
        }
    }

}