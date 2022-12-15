package ua.syt0r.kanji_dojo.parser.model

data class CharacterInfoData(
    val kanji: Char,
    val meanings: List<String>,
    val onReadings: List<String>,
    val kunReadings: List<String>,
    val jlpt: String?,
    val frequency: Int?,
    val grade: Int?
)