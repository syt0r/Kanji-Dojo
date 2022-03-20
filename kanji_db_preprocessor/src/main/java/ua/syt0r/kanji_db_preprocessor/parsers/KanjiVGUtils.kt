package ua.syt0r.kanji_db_preprocessor.parsers

import org.jsoup.Jsoup
import ua.syt0r.kanji_db_model.model.KanjiStrokesData
import ua.syt0r.svg.SvgCommandParser
import java.io.File

data class KanjiVGData(
    override val kanji: Char,
    override val strokes: List<String>
) : KanjiStrokesData

object KanjiVGUtils {

    fun parseStrokes(kanjiVGDataFolder: File): List<KanjiVGData> {
        val kanjiFiles = kanjiVGDataFolder.listFiles()
            ?: throw IllegalStateException("No files found")

        println("${kanjiFiles.size} files found, start parsing")

        return kanjiFiles
            .associate { file -> parseSvgFile(file).let { it.kanji to it } } // Removes doubles
            .values
            .toList()
    }

    private fun parseSvgFile(file: File): KanjiVGData {
        val document = Jsoup.parse(file, Charsets.UTF_8.name())
        val strokesElement = document.selectFirst("[id*=StrokePath]")

        val kanji = Integer.parseInt(
            file.nameWithoutExtension.split("-").first(),
            16
        ).toChar()
        val strokes = strokesElement.select("path").map { it.attr("d") }

        // Validation
        strokes.forEach { SvgCommandParser.parse(it) }

        return KanjiVGData(kanji, strokes)
    }

}




