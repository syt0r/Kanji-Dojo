package ua.syt0r.kanji_db_preprocessor.parsers

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

data class KanjiDictData(
    val kanji: Char,
    val onyomi: List<String>,
    val kunyomi: List<String>,
    val grade: String,
    val jlpt: String,
    val meaning: List<String>,
    val compactMeaning: List<String>
)

object KanjiumUtils {

    fun getKanjiDictData(kanjiumFile: File): List<KanjiDictData> {
        val kanjiumDatabase = Database.connect("jdbc:sqlite:${kanjiumFile.absolutePath}")
        return transaction(kanjiumDatabase) { KanjiDictTable.selectAll().toList() }
            .map {
                KanjiDictData(
                    kanji = it[KanjiDictTable.kanji].first(),
                    onyomi = it[KanjiDictTable.onyomi].parseArray(),
                    kunyomi = it[KanjiDictTable.kunyomi].parseArray(),
                    grade = it[KanjiDictTable.grade],
                    jlpt = it[KanjiDictTable.jlpt],
                    meaning = it[KanjiDictTable.meaning].parseArray(),
                    compactMeaning = it[KanjiDictTable.compactMeaning].parseArray()
                )
            }
    }

    private fun String.parseArray(): List<String> = split(',', ';', '、')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()

}

private object KanjiDictTable : Table(name = "kanjidict") {

    val kanji: Column<String> = text("kanji")
    val onyomi: Column<String> = text("onyomi")
    val kunyomi: Column<String> = text("kunyomi")
    val grade: Column<String> = text("grade")
    val jlpt: Column<String> = text("jlpt")
    val meaning: Column<String> = text("meaning")
    val compactMeaning: Column<String> = text("compact_meaning")

    override val primaryKey = PrimaryKey(kanji)

}

