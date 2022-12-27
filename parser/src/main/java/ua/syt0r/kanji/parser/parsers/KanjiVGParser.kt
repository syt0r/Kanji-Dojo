package ua.syt0r.kanji.parser.parsers

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import ua.syt0r.kanji.common.svg.SvgCommandParser
import java.io.File

data class KanjiVGData(
    val character: Char,
    val item: KanjiVGDataItem,
    val strokes: List<String>
)

sealed class KanjiVGDataItem {

    data class Group(
        val element: String?,
        val position: String?,
        val radical: String?,
        val part: Int?,
        val list: List<KanjiVGDataItem>
    ) : KanjiVGDataItem()

    data class Path(val path: String) : KanjiVGDataItem()

}

fun KanjiVGDataItem.getPaths(): List<String> {
    return when (this) {
        is KanjiVGDataItem.Group -> list.flatMap { it.getPaths() }
        is KanjiVGDataItem.Path -> listOf(path)
    }
}

object KanjiVGParser {

    fun parse(kanjiVGDataFolder: File): List<KanjiVGData> {
        val kanjiFiles = kanjiVGDataFolder.listFiles()
            ?: throw IllegalStateException("No files found")

        println("${kanjiFiles.size} files found, start parsing")

        return kanjiFiles.asSequence()
            // filters out uncommon character variations (KaishoXXX, Hz, Vt)
            .filter { it.name.contains("-").not() }
            .map { parseSvgFile(it) }
            .groupBy { it.character }
            .map {
                it.value.run {
                    assert(size == 1)
                    first()
                }
            }
            .toList()
    }

    private fun parseSvgFile(file: File): KanjiVGData {
        val document = Jsoup.parse(file, Charsets.UTF_8.name())
        val strokesElement = document.selectFirst("[id*=StrokePath]")

        val fileName = file.nameWithoutExtension
        val kanji = Integer.parseInt(fileName.split("-").first(), 16).toChar()

        val item = strokesElement.selectFirst("[id=kvg:$fileName]").toItem()
        val strokes = item.getPaths()

        // Strokes validation
        strokes.forEach { SvgCommandParser.parse(it) }

        return KanjiVGData(kanji, item, strokes)
    }

    private fun Element.toItem(): KanjiVGDataItem {
        return when (val t = tagName()) {
            "path" -> KanjiVGDataItem.Path(attr("d"))
            "g" -> KanjiVGDataItem.Group(
                element = attr("kvg:element").takeIf { it.isNotEmpty() },
                position = attr("kvg:position").takeIf { it.isNotEmpty() },
                radical = attr("kvg:radical").takeIf { it.isNotEmpty() },
                part = attr("kvg:part")?.takeIf { it.isNotEmpty() }?.toInt(),
                list = children().map { it.toItem() }
            )
            else -> throw IllegalStateException("Unknown tag[$t]")
        }
    }

}




