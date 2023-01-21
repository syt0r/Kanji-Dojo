package ua.syt0r.kanji.parser.parsers

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

data class JMdictItem(
    val elements: List<JMDictElement>,
    val meanings: List<String>
)

enum class JMDictElementType { Kanji, Reading }

class JMDictElement(
    val expression: String,
    val type: JMDictElementType,
    val priorities: List<JMDictPriority>
) {
    fun priority() = priorities.minOfOrNull { it.asNumber() } ?: Int.MAX_VALUE
}

sealed class JMDictPriority {

    abstract fun asNumber(): Int

    class News(
        private val number: Int
    ) : JMDictPriority() {
        override fun asNumber() = if (number == 1) 11573 else 22160
    }

    class Ichi(
        private val number: Int
    ) : JMDictPriority() {
        override fun asNumber() = if (number == 1) 8508 else 17039
    }

    class Spec(
        private val number: Int
    ) : JMDictPriority() {
        override fun asNumber() = if (number == 1) 1317 else 2929
    }

    class Gai(
        private val number: Int
    ) : JMDictPriority() {
        override fun asNumber() = if (number == 1) 20000 else 40000
    }

    class NF(
        private val number: Int
    ) : JMDictPriority() {
        override fun asNumber() = number * 500
    }

}


object JMdictParser {

    fun parse(file: File): List<JMdictItem> {
        return Jsoup.parse(file, Charsets.UTF_8.name())
            .select("entry")
            .map { dicEntry ->

                val elements = dicEntry.children().mapNotNull {
                    when (it.tagName()) {
                        "k_ele" -> JMDictElement(
                            expression = it.selectFirst("keb")?.text()!!,
                            type = JMDictElementType.Kanji,
                            priorities = it.select("ke_pri").map { it.toPriority() }
                        )
                        "r_ele" -> JMDictElement(
                            expression = it.selectFirst("reb")?.text()!!,
                            type = JMDictElementType.Reading,
                            priorities = it.select("re_pri").map { it.toPriority() }
                        )
                        else -> null
                    }
                }

                JMdictItem(
                    elements = elements,
                    meanings = dicEntry.select("gloss")
                        .map { it.text() }
                        .also { assert(it.isNotEmpty()) }
                )
            }
    }

    private fun Element.toPriority(): JMDictPriority {
        val value = text()
        return when {
            value.startsWith("news") -> JMDictPriority.News(value.takeLast(1).toInt())
            value.startsWith("ichi") -> JMDictPriority.Ichi(value.takeLast(1).toInt())
            value.startsWith("spec") -> JMDictPriority.Spec(value.takeLast(1).toInt())
            value.startsWith("gai") -> JMDictPriority.Gai(value.takeLast(1).toInt())
            value.startsWith("nf") -> JMDictPriority.NF(value.takeLast(2).toInt())
            else -> throw IllegalStateException("unknown priority $value")
        }
    }

}