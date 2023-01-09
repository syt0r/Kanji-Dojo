package ua.syt0r.kanji.common.db.entity

data class CharacterRadical(
    val character: String,
    val radical: String,
    val startPosition: Int,
    val strokesCount: Int
)