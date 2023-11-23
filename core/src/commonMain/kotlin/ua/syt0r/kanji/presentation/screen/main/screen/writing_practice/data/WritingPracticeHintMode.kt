package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

enum class WritingPracticeHintMode(
    val message: () -> String
) {
    OnlyNew(
        message = { "Study new" }
    ),
    All(
        message = { "For all" }
    ),
    None(
        message = { "Never" }
    ),
}