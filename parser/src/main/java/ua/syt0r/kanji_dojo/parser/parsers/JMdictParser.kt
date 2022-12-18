package ua.syt0r.kanji_dojo.parser.parsers

import org.jsoup.Jsoup
import java.io.File

data class JMdictItem(
    val expression: String,
    val meanings: List<String>,
    val priority: List<String> // news1/2, ichi1/2, spec1/2, gail1/2, nfxx
)

object JMdictParser {

    fun parse(file: File = File("data/JMdict_e")): List<JMdictItem> {
        return Jsoup.parse(file, Charsets.UTF_8.name())
            .select("entry")
            .map { dicEntry ->

                val kanjiElement = dicEntry.selectFirst("k_ele")
                val kanjiExpression = kanjiElement?.selectFirst("keb")?.text()
                val kanjiPriorities = kanjiElement?.select("ke_pri")?.eachText()

                val kanaElement = dicEntry.selectFirst("r_ele")
                val kanaExpression = kanaElement?.selectFirst("reb")?.text()
                val kanaPriorities = kanaElement?.select("ke_pri")?.eachText()

                JMdictItem(
                    expression = kanjiExpression
                        ?: kanaExpression
                        ?: throw IllegalStateException(dicEntry.toString()),
                    meanings = dicEntry.select("gloss")
                        .map { it.text() }
                        .also { assert(it.isNotEmpty()) },
                    priority = kanjiPriorities
                        ?: kanaPriorities
                        ?: throw IllegalStateException(dicEntry.toString())
                )
            }
    }

}