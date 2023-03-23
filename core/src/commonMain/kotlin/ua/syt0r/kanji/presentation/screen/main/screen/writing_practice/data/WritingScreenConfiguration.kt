package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import androidx.compose.runtime.State

data class WritingScreenConfiguration(
    val shouldHighlightRadicals: State<Boolean>,
    val noTranslationsLayout: Boolean,
    val leftHandedMode: Boolean
)