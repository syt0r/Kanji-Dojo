package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.DictionaryReadingTableSchema
import ua.syt0r.kanji.common.db.schema.DictionaryReadingTableSchema.Columns

object DictionaryReadingsTable : Table(DictionaryReadingTableSchema.name) {

    val dictionaryEntryId: Column<Long> = long(Columns.dictionaryEntryId)
    val kanjiExpression: Column<String?> = text(Columns.kanjiExpression).nullable()
    val kanaExpression: Column<String> = text(Columns.kanaExpression)
    val furigana: Column<String?> = text(Columns.furigana).nullable()
    val rank: Column<Int> = integer(Columns.rank)

}