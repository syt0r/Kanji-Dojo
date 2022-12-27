package ua.syt0r.kanji.presentation.screen.screen.practice_create.data

import ua.syt0r.kanji.common.CharactersClassification

sealed class CreatePracticeConfiguration {

    object NewPractice : CreatePracticeConfiguration()

    data class EditExisting(
        val practiceId: Long
    ) : CreatePracticeConfiguration()

    data class Import(
        val title: String,
        val classification: CharactersClassification
    ) : CreatePracticeConfiguration()

}