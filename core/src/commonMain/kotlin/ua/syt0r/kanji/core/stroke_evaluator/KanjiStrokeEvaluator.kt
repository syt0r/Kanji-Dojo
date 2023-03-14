package ua.syt0r.kanji.core.stroke_evaluator

import androidx.compose.ui.graphics.Path

interface KanjiStrokeEvaluator {
    fun areStrokesSimilar(first: Path, second: Path): Boolean
}