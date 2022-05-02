package ua.syt0r.kanji_dojo.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_dojo.shared.db.KanjiMeaningTable


object KanjiMeanings : Table(name = KanjiMeaningTable.name) {

    val kanji: Column<String> = text(KanjiMeaningTable.Columns.kanji)
    val meaning: Column<String> = text(KanjiMeaningTable.Columns.meaning)
    val priority: Column<Int> = integer(KanjiMeaningTable.Columns.priority)

    override val primaryKey = PrimaryKey(kanji, meaning, priority)

}