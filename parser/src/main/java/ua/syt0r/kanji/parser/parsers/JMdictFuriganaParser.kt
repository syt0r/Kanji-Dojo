package ua.syt0r.kanji.parser.parsers

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class JMdictFuriganaItem(
    @SerialName("text")
    @SerializedName("text")
    val kanjiExpression: String,

    @SerialName("reading")
    @SerializedName("reading")
    val kanaExpression: String,

    @SerialName("furigana")
    @SerializedName("furigana")
    val furigana: List<JMDictFuriganaRubyItem>
)

@Serializable
data class JMDictFuriganaRubyItem(
    val ruby: String,
    val rt: String? = null
)

object JMdictFuriganaParser {

    fun parse(file: File): List<JMdictFuriganaItem> {
        val json = file.readLines().joinToString("")
        return Gson().fromJson(
            json,
            object : TypeToken<List<JMdictFuriganaItem>>() {}.type
        )
        // TODO fix issues and remove Gson dependency return Json.decodeFromString(json)
    }

}