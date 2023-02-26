package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.KanjiDataTableSchema

object KanjiDataTable : Table(name = KanjiDataTableSchema.name) {

    val kanji: Column<String> = text(KanjiDataTableSchema.Columns.kanji)
    val frequency: Column<Int?> = integer(KanjiDataTableSchema.Columns.frequency).nullable()

    override val primaryKey = PrimaryKey(kanji)

}