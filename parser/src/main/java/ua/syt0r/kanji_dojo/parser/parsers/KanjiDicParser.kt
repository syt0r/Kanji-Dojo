package ua.syt0r.kanji_dojo.parser.parsers

import org.jsoup.Jsoup
import java.io.File

data class KanjiDicEntry(
    val kanji: Char,
    val meanings: List<String>,
    val onReadings: List<String>,
    val kunReadings: List<String>,
    val grade: Int?,
    val frequency: Int?
)

object KanjiDicParser {

    fun parse(file: File): List<KanjiDicEntry> {
        return Jsoup.parse(file, Charsets.UTF_8.name()).select("character").map {

            val readingElements = it.select("reading")

            val kunReadings = readingElements
                .filter { it.attr("r_type").startsWith("ja_kun") }
                .map { it.text() }

            val onReadings = readingElements
                .filter { it.attr("r_type").startsWith("ja_on") }
                .map { it.text() }

            val meanings = it.select("meaning")
                .filter { !it.hasAttr("m_lang") }
                .map { it.text() }

            KanjiDicEntry(
                kanji = it.selectFirst("literal").text().first(),
                meanings = meanings,
                kunReadings = kunReadings,
                onReadings = onReadings,
                grade = it.select("grade").text().toIntOrNull(),
                frequency = it.select("freq").text().toIntOrNull()
            )
        }

    }

}
