package ua.syt0r.kanji.parser.model

data class CharacterInfoData(
    val kanji: Char,
    val meanings: List<String>,
    val onReadings: List<String>,
    val kunReadings: List<String>,
    val frequency: Int?,
    val grade: Int?
) {

    fun isInvalid(): Boolean {
        val emptyMeanings = meanings.isEmpty()
        val emptyReadings = (onReadings.isEmpty() && kunReadings.isEmpty())
        return emptyMeanings || emptyReadings
    }

}

val ExtraCharactersInfoData = listOf<CharacterInfoData>(
    CharacterInfoData(
        kanji = '々',
        meanings = listOf("Kanji repetition mark"),
        onReadings = listOf("ノマ"),
        kunReadings = listOf("のま"),
        frequency = null,
        grade = null
    )
)