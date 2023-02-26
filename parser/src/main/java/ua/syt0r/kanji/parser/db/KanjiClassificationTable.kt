package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.KanjiClassificationTableSchema
import ua.syt0r.kanji.common.db.schema.KanjiClassificationTableSchema.Columns

object KanjiClassificationTable : Table(name = KanjiClassificationTableSchema.name) {

    val kanji: Column<String> = text(Columns.kanji)
    val classification: Column<String> = text(Columns.classification)

    override val primaryKey = PrimaryKey(kanji, classification)

}