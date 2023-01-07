package ua.syt0r.kanji.parser.model

import ua.syt0r.kanji.common.db.entity.FuriganaDBEntity

data class Word(
    val expression: String,
    val meanings: List<String>,
    val furigana: List<FuriganaDBEntity>,
    val priority: Int
)
