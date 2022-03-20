package ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.data

class EnteredKanji(
    val kanji: String,
    val isKnown: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnteredKanji

        if (kanji != other.kanji) return false

        return true
    }

    override fun hashCode(): Int {
        return kanji.hashCode()
    }

}
