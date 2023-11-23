package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import androidx.compose.runtime.State

data class WritingScreenConfiguration(
    val characters: List<String>,
    val shuffle: Boolean,
    val hintMode: WritingPracticeHintMode,
    val noTranslationsLayout: Boolean,
    val leftHandedMode: Boolean,
)

data class WritingScreenLayoutConfiguration(
    val noTranslationsLayout: Boolean,
    val radicalsHighlight: State<Boolean>,
    val leftHandedMode: Boolean
)