package ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.data

sealed class CreatePracticeConfiguration {

    object NewPractice : CreatePracticeConfiguration()

    data class EditExisting(
        val practiceId: Long
    ) : CreatePracticeConfiguration()

    data class Import(
        val className: String,
        val practiceDefaultName: String
    ) : CreatePracticeConfiguration()

}