package ua.syt0r.kanji.parser.model

import ua.syt0r.kanji.common.CharactersClassification

data class CharacterClass(
    val char: String,
    val clazz: CharactersClassification
)