package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeMode

interface PracticeMode {
    val title: String
}

val AvailablePracticeModes = WritingPracticeMode.values()