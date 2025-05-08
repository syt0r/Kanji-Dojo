package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.app_data.data.buildFuriganaString
import ua.syt0r.kanji.core.logger.Logger


sealed interface TextAnalysisNode {

    data class Text(
        val value: String
    ) : TextAnalysisNode

    data class Word(
        val sequence: Long,
        val text: String,
        val kana: String,
        val reading: String,
        val glossary: List<Glossary>,
        val combinedPartOfSpeechList: List<PartOfSpeech>,
        val highlightPartOfSpeech: PartOfSpeech?,
        val furigana: FuriganaString
    ) : TextAnalysisNode

    data class AlternativeWords(
        val words: List<Word>
    ) : TextAnalysisNode

    data class Error(
        val text: String?
    ) : TextAnalysisNode

    data class Glossary(
        val definition: String,
        val partOfSpeech: Set<PartOfSpeech> = emptySet()
    )

    enum class PartOfSpeech(regexPattern: String) {
        Noun("n"),
        Verb("v.*"),
        Adj("adj.*"),
        Prt("prt"),
        Suf("suf"),
        Exp("exp");

        val regex = Regex(regexPattern)
    }

}

class TextAnalysisParser {

    fun parseIchiranJson(jsonArray: JsonArray): List<TextAnalysisNode> {
        return jsonArray.flatMap { sentencePart -> parseSentencePart(sentencePart) }
    }

    private fun parseSentencePart(sentencePart: JsonElement): List<TextAnalysisNode> {
        return when (sentencePart) {
            is JsonPrimitive -> listOf(
                TextAnalysisNode.Text(sentencePart.content)
            )

            is JsonArray -> sentencePart
                .map { sentencePartBlock -> sentencePartBlock.jsonArray }
                .flatten()
                .filterIsInstance<JsonArray>() // ignore ends with some number
                .map { wordsGroup -> wordsGroup.jsonArray }
                .flatten()
                .flatMap { parseRomajiArrayStruct(it) }

            else -> emptyList<TextAnalysisNode>()
        }
    }

    private fun parseRomajiArrayStruct(word: JsonElement): List<TextAnalysisNode> {
        return runCatching {
            val romaji: String
            val data: JsonObject

            word.jsonArray.let {
                romaji = it[0].jsonPrimitive.content
                data = it[1].jsonObject
            }

            data.romajiStructDataAsNodes()
        }.getOrElse {
            Logger.d("Couldn't parse word[$word], reason[$it]")
            listOf(TextAnalysisNode.Error(null))
        }
    }

    private fun JsonObject.romajiStructDataAsNodes(): List<TextAnalysisNode> {
        val alternatives = get("alternative")?.jsonArray

        return when {
            alternatives != null -> {
                val alternativeWords = alternatives
                    .flatMap { it.jsonObject.singleOrCompoundStructAsNodes() }

                val validWords = alternativeWords.filterIsInstance<TextAnalysisNode.Word>()

                val node = if (validWords.isNotEmpty()) {
                    TextAnalysisNode.AlternativeWords(validWords)
                } else {
                    validWords.firstOrNull() ?: TextAnalysisNode.Error(null)
                }

                listOf(node)
            }

            else -> singleOrCompoundStructAsNodes()
        }
    }

    private fun JsonObject.singleOrCompoundStructAsNodes(): List<TextAnalysisNode> {
        val compound = get("compound")?.jsonArray
        return when {
            compound != null -> {
                val components = getValue("components").jsonArray
                components.map { it.jsonObject.asWordNode() }
            }

            else -> {
                listOf(asWordNode())
            }
        }
    }

    private fun JsonObject.asWordNode(): TextAnalysisNode = runCatching {
        val glossary = getGlossaries()

        val text = get("text")!!.jsonPrimitive.content.cleaned()
        val kana = get("kana")!!.jsonPrimitive.content.cleaned()
        val reading = get("reading")!!.jsonPrimitive.content.cleaned()

        runCatching {
            val furigana = when {
                text == kana -> buildFuriganaString { append(text) }
                else -> buildFuriganaString {
                    val commonPrefix = text.commonPrefixWith(kana)
                    val commonSuffix = text.commonSuffixWith(kana)

                    val middleKanji = text.substring(
                        commonPrefix.length,
                        text.length - commonSuffix.length
                    )

                    val middleKana = kana.substring(
                        commonPrefix.length,
                        kana.length - commonSuffix.length
                    )

                    if (commonPrefix.isNotEmpty()) append(commonPrefix)
                    append(middleKanji, middleKana)
                    if (commonSuffix.isNotEmpty()) append(commonSuffix)
                }
            }

            val combinedPartOfSpeechList = glossary
                .asSequence()
                .flatMap { it.partOfSpeech }
                .distinct()
                .sortedBy { it.ordinal }
                .toList()

            TextAnalysisNode.Word(
                sequence = get("seq")!!.jsonPrimitive.long,
                text = get("text")!!.jsonPrimitive.content.cleaned(),
                kana = kana,
                reading = reading,
                furigana = furigana,
                combinedPartOfSpeechList = combinedPartOfSpeechList,
                highlightPartOfSpeech = combinedPartOfSpeechList.firstOrNull(),
                glossary = glossary
            )
        }.getOrElse {
            TextAnalysisNode.Error(text)
        }

    }.getOrElse {
        TextAnalysisNode.Error(null)
    }

    private fun JsonObject.getGlossaries(): List<TextAnalysisNode.Glossary> {
        val conjugations = get("conj")?.jsonArray?.takeIf { it.isNotEmpty() }
        if (conjugations != null) {
            return conjugations
                .flatMap { it.jsonObject.getValue("gloss").jsonArray }
                .map { it.jsonObject.asGlossary() }
        }

        val glosses = get("gloss")?.jsonArray?.takeIf { it.isNotEmpty() }
        if (glosses != null) {
            return glosses.map { it.jsonObject.asGlossary() }
        }

        val suffix = get("suffix")?.jsonPrimitive?.content
        if (suffix != null) {
            return listOf(TextAnalysisNode.Glossary(suffix))
        }

        val counter = get("counter")?.jsonObject
        if (counter != null) {
            val value = counter.getValue("value").jsonPrimitive.content
            return listOf(TextAnalysisNode.Glossary(value))
        }

        error("No definitions found")
    }

    private fun JsonObject.asGlossary(): TextAnalysisNode.Glossary {
        return TextAnalysisNode.Glossary(
            definition = get("gloss")!!.jsonPrimitive.content,
            partOfSpeech = get("pos")!!.jsonPrimitive.content
                .removeSurrounding("[", "]")
                .split(",")
                .mapNotNull { value ->
                    TextAnalysisNode.PartOfSpeech
                        .entries
                        .find { it.regex.matches(value) }
                }
                .toSet()
        )
    }

    private val cleanupRegex = Regex("[\\s\\u200B-\\u200D\\uFEFF\\u2060\\u00AD\\p{Cf}]")
    private fun String.cleaned(): String = trim().replace(cleanupRegex, "")

}