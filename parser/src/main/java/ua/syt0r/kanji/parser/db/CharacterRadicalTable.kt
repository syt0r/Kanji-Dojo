package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.CharacterRadicalTableSchema

object CharacterRadicalTable : Table(name = CharacterRadicalTableSchema.name) {

    val character: Column<String> = text(CharacterRadicalTableSchema.Columns.character)
    val radical: Column<String> = text(CharacterRadicalTableSchema.Columns.radical)
    val startStrokeIndex: Column<Int> = integer(CharacterRadicalTableSchema.Columns.startStrokeIndex)
    val strokes: Column<Int> = integer(CharacterRadicalTableSchema.Columns.strokesCount)

    override val primaryKey = PrimaryKey(character, radical, startStrokeIndex)

}