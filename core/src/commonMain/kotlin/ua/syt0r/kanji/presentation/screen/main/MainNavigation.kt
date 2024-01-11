package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import ua.syt0r.kanji.core.japanese.CharacterClassification

interface MainNavigationState {
    fun navigateBack()
    fun popUpToHome()
    fun navigate(destination: MainDestination)
}

@Composable
expect fun rememberMainNavigationState(): MainNavigationState

@Composable
expect fun MainNavigation(state: MainNavigationState)

@Serializable
sealed interface MainDestination {

    @Serializable
    object Home : MainDestination

    @Serializable
    object About : MainDestination

    @Serializable
    object ImportPractice : MainDestination

    @Serializable
    sealed interface CreatePractice : MainDestination {

        @Serializable
        object New : CreatePractice

        @Serializable
        data class EditExisting(
            val practiceId: Long
        ) : CreatePractice

        @Serializable
        data class Import(
            val title: String,
            val classification: CharacterClassification
        ) : CreatePractice

    }

    @Serializable
    data class PracticePreview(
        val id: Long
    ) : MainDestination

    @Serializable
    sealed interface Practice : MainDestination {

        @Serializable
        data class Writing(
            val practiceId: Long,
            val characterList: List<String>
        ) : Practice

        @Serializable
        data class Reading(
            val practiceId: Long,
            val characterList: List<String>
        ) : Practice

    }

    @Serializable
    data class KanjiInfo(
        val character: String
    ) : MainDestination

}
