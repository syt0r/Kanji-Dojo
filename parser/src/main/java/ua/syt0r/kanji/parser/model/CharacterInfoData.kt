package ua.syt0r.kanji.parser.model

data class CharacterInfoData(
    val kanji: Char,
    val meanings: List<String>,
    val onReadings: List<String>,
    val kunReadings: List<String>,
    val frequency: Int?,
    val grade: Int?
)