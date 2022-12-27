package ua.syt0r.kanji.common.db

object KanjiReadingTable {

    const val name = "readings"

    object Columns {
        const val kanji = "kanji"
        const val readingType = "reading_type"
        const val reading = "reading"
    }

    enum class ReadingType(val value: String) {
        ON("on"),
        KUN("kun")
    }

}