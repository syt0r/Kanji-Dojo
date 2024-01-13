package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import androidx.compose.ui.graphics.Path

data class StrokeInputData(
    val userPath: Path,
    val kanjiPath: Path
)

sealed interface StrokeProcessingResult {

    data class Correct(
        val userPath: Path,
        val kanjiPath: Path
    ) : StrokeProcessingResult

    data class Mistake(val hintStroke: Path) : StrokeProcessingResult

}
