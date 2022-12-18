package ua.syt0r.kanji_dojo.parser.model

import ua.syt0r.kanji_dojo.shared.db.FuriganaDBEntity

data class Word(
    val expression: String,
    val meanings: List<String>,
    val furigana: List<FuriganaDBEntity>,
    val priority: Int
)
