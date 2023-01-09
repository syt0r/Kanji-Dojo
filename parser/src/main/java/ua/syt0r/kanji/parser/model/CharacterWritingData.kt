package ua.syt0r.kanji.parser.model

import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.common.db.entity.Radical

data class CharacterWritingData(
    val character: Char,
    val strokes: List<String>,
    val standardRadicals: List<Radical>,
    val allRadicals: List<CharacterRadical>
)