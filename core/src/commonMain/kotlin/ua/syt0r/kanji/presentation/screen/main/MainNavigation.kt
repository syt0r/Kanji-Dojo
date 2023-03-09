package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.common.CharactersClassification

interface MainNavigationState {
    fun navigateBack()
    fun popUpToHome()
    fun navigate(destination: MainDestination)
}

@Composable
expect fun rememberMainNavigationState(): MainNavigationState

@Composable
expect fun MainNavigation(state: MainNavigationState)

sealed interface MainDestination {

    object Home : MainDestination
    object About : MainDestination

    object ImportPractice : MainDestination
    sealed interface CreatePractice : MainDestination {

        object New : CreatePractice

        data class EditExisting(
            val practiceId: Long
        ) : CreatePractice

        data class Import(
            val title: String,
            val classification: CharactersClassification
        ) : CreatePractice

    }

    data class PracticePreview(
        val id: Long
    ) : MainDestination

    sealed interface Practice : MainDestination {

        data class Writing(
            val practiceId: Long,
            val characterList: List<String>,
            val isStudyMode: Boolean
        ) : Practice

        data class Reading(
            val practiceId: Long,
            val characterList: List<String>
        ) : Practice

    }

    data class KanjiInfo(
        val character: String
    ) : MainDestination

}
