package ua.syt0r.kanji_vg_parser

import com.google.gson.Gson
import org.jsoup.Jsoup
import ua.syt0r.kanji_model.TmpKanjiData
import ua.syt0r.svg_parser.SvgCommandParser
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {

    println("Start")

    val kanjiFolder = Paths.get("kanji-vg-data/").toFile()

    assert(kanjiFolder.exists())

    val kanjiFiles =
        kanjiFolder.listFiles()?.toSet() ?: throw IllegalStateException("No files found")

    println("${kanjiFiles.size} files found")

    val data = kanjiFiles.mapIndexed { index, file ->
        print("parsing kanji $index/${kanjiFiles.size}")
        val kanji = parseSvgFile(file)
        println(" ${kanji.char}")
        kanji
    }

    if (validateSvg(data))
        exportData(data)

}

fun parseSvgFile(file: File): TmpKanjiData {
    val document = Jsoup.parse(file, Charsets.UTF_8.name())
    val strokesElement = document.selectFirst("[id*=StrokePath]")

    val kanji = Integer.parseInt(file.nameWithoutExtension, 16).toChar()
    val strokes = strokesElement.select("path").map { it.attr("d") }

    return TmpKanjiData(kanji, strokes)
}

fun validateSvg(kanjiList: List<TmpKanjiData>): Boolean {
    var errorsFound = false

    val errors = mutableListOf<Throwable>()

    kanjiList.forEach { kanji ->
        kanji.strokes.forEach {
            runCatching {
                SvgCommandParser.parse(it)
            }.getOrElse {
                println("Unsupported svg found for kanji[${kanji.char}], error[$it]")
                errors.add(it)
                errorsFound = true
            }
        }
    }

    val uniqueErrors = errors.map { it.message }.distinct()
    println("Unique errors count [${uniqueErrors.size}]")

    return !errorsFound
}

fun exportData(kanjiList: List<TmpKanjiData>) {
    println("Start exporting")
    //TODO DB
    val gson = Gson()
    val json = gson.toJson(kanjiList)

    File("data.json").writeText(json)
    println("Finish exporting")
}