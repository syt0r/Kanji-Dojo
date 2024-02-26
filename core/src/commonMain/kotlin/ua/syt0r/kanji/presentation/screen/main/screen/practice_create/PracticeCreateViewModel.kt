package ua.syt0r.kanji.presentation.screen.main.screen.practice_create

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.ProcessingStatus
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

                val validatedData = withContext(Dispatchers.IO) {
                    validateCharactersUseCase.processInput(input = data.characters.joinToString(""))
                }

                state.value = ScreenState.Loaded(
                    processingStatus = ProcessingStatus.Loaded,
                    characters = validatedData.detectedCharacter,
                    charactersToRemove = emptySet(),
                    wasEdited = false,
                    initialPracticeTitle = data.title,
                )

                validatedData.unknownCharacters.forEach {
                    analyticsManager.sendEvent("import_unknown_character") {
                        put("character", it)
                    }
                }
            }
        }
    }

    override suspend fun submitUserInput(input: String): ValidationResult {
        val result: Deferred<ValidationResult> = viewModelScope.async {

            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(processingStatus = ProcessingStatus.ProcessingInput)


            val processingResult = withContext(Dispatchers.IO) {
                val processingResult = validateCharactersUseCase.processInput(input)
                processingResult
            }

            val updatedCharacters = withContext(Dispatchers.IO) {
                screenState.characters + processingResult.detectedCharacter
            }

            state.value = screenState.copy(
                processingStatus = ProcessingStatus.Loaded,
                characters = updatedCharacters,
                wasEdited = screenState.wasEdited || updatedCharacters != screenState.characters
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
                    val updatedCharacters = charactersToRemove.plus(character)
                    copy(
                        charactersToRemove = updatedCharacters,
                        wasEdited = true
                    )
                }

                else -> {
                    copy(
                        characters = characters.minus(character),
                        wasEdited = true
                    )
                }
            }
        }
    }

    override fun cancelRemoval(character: String) {
        val screenState = state.value as ScreenState.Loaded
        state.value = screenState.run {
            copy(charactersToRemove = charactersToRemove.minus(character))
        }
    }

    override fun savePractice(title: String) {
        viewModelScope.launch {
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(processingStatus = ProcessingStatus.Saving)

            withContext(Dispatchers.IO) {
                savePracticeUseCase.save(configuration, title, screenState)
                analyticsManager.sendEvent("removed_items") {
                    put("removed_items", screenState.charactersToRemove.size)
                }
            }

            state.value = screenState.copy(processingStatus = ProcessingStatus.SaveCompleted)
        }
    }

    override fun deletePractice() {
        viewModelScope.launch {
            val screenState = state.value as ScreenState.Loaded
            state.value = screenState.copy(processingStatus = ProcessingStatus.Deleting)

            val practiceId = configuration
                .let { it as MainDestination.CreatePractice.EditExisting }
                .practiceId

            withContext(Dispatchers.IO) { deletePracticeUseCase.delete(practiceId) }

            state.value = screenState.copy(processingStatus = ProcessingStatus.DeleteCompleted)
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