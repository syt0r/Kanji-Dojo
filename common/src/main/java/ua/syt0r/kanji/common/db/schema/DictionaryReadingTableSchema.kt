package ua.syt0r.kanji.common.db.schema

object DictionaryReadingTableSchema {

    const val name = "dic_reading"

    object Columns {
        const val dictionaryEntryId = "dic_entry_id"
        const val kanjiExpression = "expression"
        const val kanaExpression = "kana_expression"
        const val furigana = "furigana"
        const val rank = "rank"
    }

}