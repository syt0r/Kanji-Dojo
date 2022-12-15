package ua.syt0r.kanji_dojo.parser.parsers

import org.jsoup.Jsoup
import ua.syt0r.kanji_dojo.shared.svg.SvgCommandParser
import java.io.File

data class KanjiVGData(
    val character: Char,
    val strokes: List<String>
)

object KanjiVGParser {

    fun parse(kanjiVGDataFolder: File): List<KanjiVGData> {
        val kanjiFiles = kanjiVGDataFolder.listFiles()
            ?: throw IllegalStateException("No files found")

        println("${kanjiFiles.size} files found, start parsing")

        return kanjiFiles
            .associate { file -> parseSvgFile(file).let { it.character to it } } // Removes doubles
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




