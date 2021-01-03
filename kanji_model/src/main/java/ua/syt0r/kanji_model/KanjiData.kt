package ua.syt0r.kanji_model

data class TmpKanjiData(
    val char: Char,
    val strokes: List<String>
)

data class KanjiData(
    val char: Char,
    val strokesCount: Int,
    val radicals: List<KanjiData>
)

data class KanjiStroke(
    val svgPath: String
)