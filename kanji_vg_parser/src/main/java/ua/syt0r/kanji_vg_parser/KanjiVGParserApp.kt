package ua.syt0r.kanji_vg_parser

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import ua.syt0r.kanji_model.KanjiData
import ua.syt0r.kanji_vg_parser.db.StrokesTable
import ua.syt0r.svg_parser.SvgCommandParser
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {

    println("Start")

    val kanjiFolder = Paths.get("kanji-vg/").toFile()

    val kanjiFiles =
        kanjiFolder.listFiles()?.toSet() ?: throw IllegalStateException("No files found")

    println("${kanjiFiles.size} files found")

    val data = kanjiFiles.mapIndexed { index, file ->
        print("parsing kanji $index/${kanjiFiles.size}")
        val kanji = parseSvgFile(file)
        println(" ${kanji.kanji}")
        kanji
    }

    if (validateSvg(data))
        exportData(data)

}

data class KD(
    override val kanji: Char,
    override val strokes: List<String>
) : KanjiData

fun parseSvgFile(file: File): KanjiData {
    val document = Jsoup.parse(file, Charsets.UTF_8.name())
    val strokesElement = document.selectFirst("[id*=StrokePath]")

    val kanji = Integer.parseInt(file.nameWithoutExtension, 16).toChar()
    val strokes = strokesElement.select("path").map { it.attr("d") }

    return KD(kanji, strokes)
}

fun validateSvg(kanjiList: List<KanjiData>): Boolean {
    var errorsFound = false

    val errors = mutableListOf<Throwable>()

    kanjiList.forEach { kanji ->
        kanji.strokes.forEach {
            runCatching {
                SvgCommandParser.parse(it)
            }.getOrElse {
                println("Unsupported svg found for kanji[${kanji.kanji}], error[$it]")
                errors.add(it)
                errorsFound = true
            }
        }
    }

    val uniqueErrors = errors.map { it.message }.distinct()
    println("Unique errors count [${uniqueErrors.size}]")

    return !errorsFound
}

fun exportData(kanjiList: List<KanjiData>) {
    println("Start exporting")

    val kanjiStrokesDb = File("app/src/main/assets/kanji-db.sqlite")
    if (kanjiStrokesDb.exists())
        kanjiStrokesDb.delete()
    kanjiStrokesDb.createNewFile()
    print(kanjiStrokesDb.absolutePath)

    Database.connect("jdbc:sqlite:${kanjiStrokesDb.absolutePath}")

    transaction {

        addLogger(StdOutSqlLogger)

        SchemaUtils.create(StrokesTable)

        kanjiList.forEach { kanjiData ->

            kanjiData.strokes.forEachIndexed { index, path ->

                StrokesTable.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[strokeNumber] = index
                    it[strokePath] = path
                }

            }

        }

    }

    println("Finish exporting")
}