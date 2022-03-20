package ua.syt0r.kanji.presentation.screen.screen.writing_practice.data

import androidx.compose.ui.graphics.Path

data class KanjiData(
    val kanji: String,
    val on: List<String>,
    val kun: List<String>,
    val meanings: List<String>,
    val strokes: List<Path>
)