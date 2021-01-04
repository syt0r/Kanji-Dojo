package ua.syt0r.kanji.core.curve_evaluator

interface CurveEvaluatorContract {

    interface CurveEvaluator {

        fun areCurvesSimilar(
            predefinedData: List<FloatArray>,
            userInputData: List<FloatArray>
        ): Boolean

    }

}