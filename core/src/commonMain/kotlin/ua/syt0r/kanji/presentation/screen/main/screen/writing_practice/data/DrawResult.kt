package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import androidx.compose.ui.graphics.Path


sealed class DrawResult {

    class Correct(
        val userDrawnPath: Path,
        val kanjiPath: Path
    ) : DrawResult()

    class Mistake(
        val path: Path
    ) : DrawResult()

    object IgnoreCompletedPractice : DrawResult()

}