package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis.parser

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.app_data.data.VocabReading
import ua.syt0r.kanji.core.app_data.data.buildFuriganaString
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.screen.main.screen.text_analysis.TextAnalysisNode

class IchiranParser {

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
                .map { parseRomajiArrayStruct(it) }

            else -> emptyList<TextAnalysisNode>()
        }
    }

    private fun parseRomajiArrayStruct(word: JsonElement): TextAnalysisNode {
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
            TextAnalysisNode.Error(null)
        }
    }

    private fun JsonObject.romajiStructDataAsNodes(): TextAnalysisNode {
        val alternatives = get("alternative")?.jsonArray

        return when {
            alternatives != null -> {
                val alternativeNodes = alternatives.map { it.jsonObject.singleOrCompound() }
                val compounds = alternativeNodes.filterIsInstance<TextAnalysisNode.Compound>()
                val compound = compounds.firstOrNull()
                TextAnalysisNode.AlternativeGroup(alternativeNodes)
            }

            else -> singleOrCompound()
        }
    }

    private fun JsonObject.singleOrCompound(): TextAnalysisNode {
        val compound = get("compound")?.jsonArray
        return when {
            compound != null -> {
                val components = getValue("components").jsonArray
                TextAnalysisNode.Compound(
                    words = components.map { it.jsonObject.asWordNode() }
                )
            }

            else -> {
                asWordNode()
            }
        }
    }

    private fun JsonObject.asWordNode(): TextAnalysisNode = runCatching {
        val glossary = getGlossaries()

        val text = get("text")!!.jsonPrimitive.content.cleaned()
        val kana = get("kana")!!.jsonPrimitive.content.cleaned()
        val reading = get("reading")!!.jsonPrimitive.content.cleaned()

        if (glossary.isEmpty()) {
            // Kana only words
            return@runCatching TextAnalysisNode.Text(text)
        }

        runCatching {
            val conjugations = get("conj")?.jsonArray
            val conjugationsVia = conjugations
                ?.flatMap { it.jsonObject["via"]?.jsonArray ?: emptyList() }
                ?.takeIf { it.isNotEmpty() }

            val conjugationReading = (conjugationsVia ?: conjugations)
                ?.firstOrNull()
                ?.jsonObject
                ?.get("reading")
                ?.jsonPrimitive
                ?.content

            val dictionaryReading: VocabReading? = conjugationReading?.let {
                val formattedKanaReadingStart = it.indexOf("【")
                when {
                    formattedKanaReadingStart == -1 -> VocabReading(
                        kanjiReading = null,
                        kanaReading = it,
                        furigana = null
                    )

                    else -> {
                        val kanji = it.substring(
                            startIndex = 0,
                            endIndex = formattedKanaReadingStart
                        ).trim()

                        val kana = it.substring(
                            startIndex = formattedKanaReadingStart.plus(1),
                            endIndex = it.indexOf("】")
                        )

                        VocabReading(
                            kanjiReading = kanji,
                            kanaReading = kana,
                            furigana = getFurigana(kanji, kana)
                        )
                    }
                }
            }

            val combinedPartOfSpeechList = glossary
                .asSequence()
                .flatMap { it.partOfSpeech }
                .distinct()
                .sortedBy { it.ordinal }
                .toList()

            TextAnalysisNode.Word(
                sequence = get("seq")?.jsonPrimitive?.long,
                text = get("text")!!.jsonPrimitive.content.cleaned(),
                reading = VocabReading(
                    kanjiReading = text.takeIf { it != kana },
                    kanaReading = kana,
                    furigana = getFurigana(text, kana),
                ),
                dictionaryReading = dictionaryReading,
                combinedPartOfSpeechList = combinedPartOfSpeechList,
                highlightPartOfSpeech = combinedPartOfSpeechList.firstOrNull(),
                glossary = glossary
            )
        }.getOrElse {
            Logger.d("parsingError[$it]")
            TextAnalysisNode.Error(text)
        }

    }.getOrElse {
        Logger.d("parsingError[$it]")
        TextAnalysisNode.Error(null)
    }

    private fun getFurigana(text: String, kana: String): FuriganaString {
        return when {
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
    }

    private fun JsonObject.getGlossaries(): List<TextAnalysisNode.Glossary> {
        val conjugations = get("conj")?.jsonArray?.takeIf { it.isNotEmpty() }
        if (conjugations != null) {
            return conjugations
                .mapNotNull { it.jsonObject["via"]?.jsonArray }
                .flatten()
                .plus(conjugations)
                .mapNotNull { it.jsonObject["gloss"]?.jsonArray }
                .flatten()
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
            val value = "Counter, " + counter.getValue("value").jsonPrimitive.content.lowercase()
            return listOf(TextAnalysisNode.Glossary(value))
        }

        return emptyList()
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