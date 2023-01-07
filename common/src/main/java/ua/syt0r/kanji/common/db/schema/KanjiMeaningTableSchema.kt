package ua.syt0r.kanji.common.db.schema

object KanjiMeaningTableSchema {

    const val name = "meanings"

    object Columns {
        const val kanji = "kanji"
        const val meaning = "meaning"
        const val priority = "priority"
    }

}