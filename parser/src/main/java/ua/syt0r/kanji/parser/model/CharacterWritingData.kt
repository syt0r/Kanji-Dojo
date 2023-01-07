package ua.syt0r.kanji.parser.model

data class CharacterWritingData(
    val character: Char,
    val strokes: List<String>,
    val standardRadicals: List<Radical>,
    val allRadicals: List<CharacterRadical>
)