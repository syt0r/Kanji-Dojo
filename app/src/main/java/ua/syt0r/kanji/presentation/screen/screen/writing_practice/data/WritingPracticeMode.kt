package ua.syt0r.kanji.presentation.screen.screen.writing_practice.data

import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PracticeMode

enum class WritingPracticeMode(override val title: String) : PracticeMode {
    Learn("Learn"),
    Review("Review")
}