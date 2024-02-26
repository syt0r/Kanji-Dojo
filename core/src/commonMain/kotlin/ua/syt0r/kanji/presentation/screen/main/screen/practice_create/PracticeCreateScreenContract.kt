package ua.syt0r.kanji.presentation.screen.main.screen.practice_create

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.ValidationResult

interface PracticeCreateScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun initialize(configuration: MainDestination.CreatePractice)
        suspend fun submitUserInput(input: String): ValidationResult

        fun remove(character: String)
        fun cancelRemoval(character: String)

        fun savePractice(title: String)
        fun deletePractice()

        fun reportScreenShown(configuration: MainDestination.CreatePractice)

    }

    enum class ProcessingStatus {
        ProcessingInput, Loaded,
        Saving, SaveCompleted,
        Deleting, DeleteCompleted
    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val processingStatus: ProcessingStatus,
            val characters: Set<String>,
            val charactersToRemove: Set<String>,
            val wasEdited: Boolean,
            val initialPracticeTitle: String?,
        ) : ScreenState()

    }

    interface LoadDataUseCase {
        suspend fun load(configuration: MainDestination.CreatePractice): CreatePracticeData
    }

    interface ValidateCharactersUseCase {
        suspend fun processInput(input: String): ValidationResult
    }

    interface SavePracticeUseCase {
        suspend fun save(
            configuration: MainDestination.CreatePractice,
            title: String,
            state: ScreenState.Loaded
        )
    }

    interface DeletePracticeUseCase {
        suspend fun delete(practiceId: Long)
    }

}

