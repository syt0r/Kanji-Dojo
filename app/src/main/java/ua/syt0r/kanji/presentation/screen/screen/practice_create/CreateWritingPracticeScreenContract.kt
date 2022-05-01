package ua.syt0r.kanji.presentation.screen.screen.practice_create

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.EnteredKanji

interface CreateWritingPracticeScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun initialize(configuration: CreatePracticeConfiguration)
        fun submitUserInput(input: String)
        fun removeCharacter(character: String)

        fun savePractice(title: String)
        fun deletePractice()

    }

    enum class DataAction {
        ProcessingInput, Loaded,
        Saving, SaveCompleted,
        Deleting, DeleteCompleted
    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val initialPracticeTitle: String?,
            val data: Set<EnteredKanji>,
            val currentDataAction: DataAction
        ) : ScreenState()

    }

}

