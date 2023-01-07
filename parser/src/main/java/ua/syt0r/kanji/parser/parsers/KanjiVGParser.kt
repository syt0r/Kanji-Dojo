package ua.syt0r.kanji.parser.parsers

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

data class KanjiVGCharacterData(
    val character: Char,
    val compound: KanjiVGCompound
)

sealed class KanjiVGCompound {

    data class Group(
        val element: String?,
        val position: String?,
        val radical: String?,
        val part: Int?,
        val partial: Boolean?,
        val variant: Boolean?,
        val childrens: List<KanjiVGCompound>,
        val startStrokeIndex: Int,
        val endStrokeIndex: Int
    ) : KanjiVGCompound()

    data class Path(
        val path: String
    ) : KanjiVGCompound()

}

object KanjiVGParser {

    fun parse(kanjiVGDataFolder: File): List<KanjiVGCharacterData> {
        val kanjiFiles = kanjiVGDataFolder.listFiles()
            ?: throw IllegalStateException("No files found")

        println("${kanjiFiles.size} files found, start parsing")

        return kanjiFiles.asSequence()
            // filters out uncommon character variations (KaishoXXX, Hz, Vt)
            .filter { it.name.contains("-").not() }
            .map {
                val fileName = it.nameWithoutExtension
                val character = Integer.parseInt(fileName.split("-").first(), 16).toChar()

                val compound = parseSvgFile(it)

                KanjiVGCharacterData(
                    character = character,
                    compound = compound
                )
            }
            .groupBy { it.character }
            .map {
                it.value.run {
                    if (size != 1) error("Character variation found for ${it.key}")
                    first()
                }
            }
            .toList()
    }

    private fun parseSvgFile(file: File): KanjiVGCompound {
        val document = Jsoup.parse(file, Charsets.UTF_8.name())
        val strokesElement = document.selectFirst("[id*=StrokePath]")
        return strokesElement.selectFirst("[id=kvg:${file.nameWithoutExtension}]")
            .parseCompound()
    }

    private fun Element.parseCompound(): KanjiVGCompound {
        return when (val t = tagName()) {
            "path" -> KanjiVGCompound.Path(attr("d"))
            "g" -> {

                val childPathIndexes = select("path").asSequence()
                    .map { it.attr("id") }
                    .map {
                        val sIndex = it.indexOfLast { it == 's' }
                        assert(sIndex != -1)
                        it.substring(sIndex + 1)
                    }
                    .map { it.toInt() }
                    .toList()

                KanjiVGCompound.Group(
                    element = attr("kvg:element").takeIf { it.isNotEmpty() },
                    position = attr("kvg:position").takeIf { it.isNotEmpty() },
                    radical = attr("kvg:radical").takeIf { it.isNotEmpty() },
                    part = attr("kvg:part")?.takeIf { it.isNotEmpty() }?.toInt(),
                    partial = attr("kvg:partial")?.toBooleanStrictOrNull(),
                    variant = attr("kvg:variant")?.toBooleanStrictOrNull(),
                    childrens = children().map { it.parseCompound() },
                    startStrokeIndex = childPathIndexes.minOrNull()?.minus(1) ?: -1,
                    endStrokeIndex = childPathIndexes.maxOrNull()?.minus(1) ?: -1
                ).apply {
                    if (startStrokeIndex == -1 || endStrokeIndex == -1) {
                        println("Error, empty group ${attr("id")}")
                    }
                }
            }
            else -> throw IllegalStateException("Unknown tag[$t]")
        }
    }

}




