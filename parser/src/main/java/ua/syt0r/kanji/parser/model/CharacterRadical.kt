package ua.syt0r.kanji.parser.model

data class CharacterRadical(
    val character: String,
    val radical: String,
    val startPosition: Int,
    val strokesCount: Int
)