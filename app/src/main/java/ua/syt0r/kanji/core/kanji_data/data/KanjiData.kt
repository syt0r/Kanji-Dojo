package ua.syt0r.kanji.core.kanji_data.data

import ua.syt0r.kanji.common.CharactersClassification

class KanjiData(
    val kanji: String,
    val frequency: Int?,
    val grade: Int?,
    val jlpt: CharactersClassification.JLPT?
)