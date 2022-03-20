package ua.syt0r.kanji.core.stroke_evaluator

interface StrokeEvaluatorContract {

    interface Evaluator {

        fun areStrokesSimilar(
            predefinedData: List<FloatArray>,
            userInputData: List<FloatArray>
        ): Boolean

    }

}