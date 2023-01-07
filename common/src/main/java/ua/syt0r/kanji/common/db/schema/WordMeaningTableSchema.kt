package ua.syt0r.kanji.common.db.schema

object WordMeaningTableSchema {

    const val name = "word_meanings"

    object Columns {
        const val expressionId = "expression_id"
        const val meaning = "meaning"
        const val priority = "priority"
    }

}