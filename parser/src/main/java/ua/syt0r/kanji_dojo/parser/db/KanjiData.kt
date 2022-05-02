package ua.syt0r.kanji_dojo.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_dojo.shared.db.KanjiDataTable

object KanjiData : Table(name = KanjiDataTable.name) {

    val kanji: Column<String> = text(KanjiDataTable.Columns.kanji)
    val grade: Column<Int?> = integer(KanjiDataTable.Columns.grade).nullable()
    val frequency: Column<Int?> = integer(KanjiDataTable.Columns.frequency).nullable()
    val jlpt: Column<String?> = text(KanjiDataTable.Columns.jlpt).nullable()

    override val primaryKey = PrimaryKey(kanji)

}