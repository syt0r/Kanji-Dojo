package ua.syt0r.kanji_vg_parser

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import ua.syt0r.kanji_model.KanjiStrokesData
import ua.syt0r.kanji_vg_parser.db.KanjiStrokes
import ua.syt0r.svg_parser.SvgCommandParser
import java.io.File
import java.nio.file.Paths

fun prepareKanjiStrokes(database: Database) {
    val kanjiFolder = Paths.get("kanji-vg/").toFile()

    val kanjiFiles = kanjiFolder.listFiles()
        ?.toSet()
        ?: throw IllegalStateException("No files found")

    println("${kanjiFiles.size} files found, start parsing")

    val kanjiData = kanjiFiles.map { file -> parseSvgFile(file) }

    writeStrokesToDB(database, kanjiData)
}

data class KanjiVGData(
    override val kanji: Char,
    override val strokes: List<String>
) : KanjiStrokesData

private fun KanjiVGData.validate() {
    strokes.forEach { SvgCommandParser.parse(it) }
}

private fun parseSvgFile(file: File): KanjiStrokesData {
    val document = Jsoup.parse(file, Charsets.UTF_8.name())
    val strokesElement = document.selectFirst("[id*=StrokePath]")

    val kanji = Integer.parseInt(file.nameWithoutExtension, 16).toChar()
    val strokes = strokesElement.select("path").map { it.attr("d") }

    return KanjiVGData(
        kanji,
        strokes
    ).apply { validate() }
}


private fun writeStrokesToDB(
    database: Database,
    kanjiList: List<KanjiStrokesData>
) = transaction(database) {
    addLogger(StdOutSqlLogger)

    SchemaUtils.create(KanjiStrokes)

    kanjiList.forEach { kanjiData ->
        kanjiData.strokes.forEachIndexed { index, path ->
            KanjiStrokes.insert {
                it[kanji] = kanjiData.kanji.toString()
                it[strokeNumber] = index
                it[strokePath] = path
            }
        }
    }
}