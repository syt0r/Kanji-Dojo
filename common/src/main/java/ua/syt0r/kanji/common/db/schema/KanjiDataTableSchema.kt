package ua.syt0r.kanji.common.db.schema

object KanjiDataTableSchema {

    const val name = "data"

    object Columns {
        const val kanji = "kanji"
        const val grade = "grade"
        const val frequency = "freq"
        const val jlpt = "jlpt"
    }

}