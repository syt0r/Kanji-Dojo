package ua.syt0r.kanji.parser.parsers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

@kotlinx.serialization.Serializable
data class JMdictFuriganaItem(
    @SerialName("text") val kanjiExpression: String,
    @SerialName("reading") val kanaExpression: String,
    @SerialName("furigana") val furigana: List<JMDictFuriganaRubyItem>
)

@kotlinx.serialization.Serializable
data class JMDictFuriganaRubyItem(
    val ruby: String,
    val rt: String?
)

object JMdictFuriganaParser {

    @OptIn(ExperimentalSerializationApi::class)
    fun parse(file: File): List<JMdictFuriganaItem> {
        val inputStream = file.inputStream()
        return Json.decodeFromStream(inputStream)
    }

}