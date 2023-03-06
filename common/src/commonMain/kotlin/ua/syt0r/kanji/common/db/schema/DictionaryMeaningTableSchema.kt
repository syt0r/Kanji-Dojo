package ua.syt0r.kanji.common.db.schema

object DictionaryMeaningTableSchema {

    const val name = "dic_meaning"

    object Columns {
        const val expressionId = "dic_entry_id"
        const val meaning = "meaning"
        const val priority = "priority"
    }

}