package ua.syt0r.kanji_dojo.parser.parsers

import org.jsoup.Jsoup
import java.io.File

data class JMdictItem(
    val expression: String,
    val meanings: List<String>
)

object JMdictParser {

    fun parse(file: File = File("data/JMdict_e")): List<JMdictItem> {
        return Jsoup.parse(file, Charsets.UTF_8.name())
            .select("entry")
            .map {
                val expression = it.selectFirst("k_ele > keb")?.text()
                    ?: it.selectFirst("r_ele > reb")?.text()

                JMdictItem(
                    expression = expression!!,
                    meanings = it.select("gloss").map { it.text() }
                )
            }
    }

}