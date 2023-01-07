package ua.syt0r.kanji.parser

import org.jetbrains.exposed.sql.Database
import ua.syt0r.kanji.common.isKana
import ua.syt0r.kanji.common.isKanji
import ua.syt0r.kanji.parser.converter.KanjiDicEntryConverter
import ua.syt0r.kanji.parser.converter.KanjiVGConverter
import ua.syt0r.kanji.parser.converter.WordConverter
import ua.syt0r.kanji.parser.parsers.JMdictFuriganaParser
import ua.syt0r.kanji.parser.parsers.JMdictParser
import ua.syt0r.kanji.parser.parsers.KanjiDicParser
import ua.syt0r.kanji.parser.parsers.KanjiVGParser
import java.io.File

private val DataFolder = File("parser_data/")
private val KanjiVGFolder = File(DataFolder, "kanji-vg/")
private val KanjiDicFile = File(DataFolder, "kanjidic2.xml")
private val JMDictFile = File(DataFolder, "JMdict_e")
private val JMDictFuriganaFile = File(DataFolder, "JmdictFurigana.json")

fun main(args: Array<String>) {

    println("Parsing character strokes (KanjiVG) ...")
    val kanjiVGData = KanjiVGParser.parse(KanjiVGFolder)
    println("Done, ${kanjiVGData.size} items found")

    println("Filtering kana and kanji...")
    val charactersWritingDataMap = kanjiVGData.asSequence()
        .filter { it.character.isKana() || it.character.isKanji() }
        .associate { it.character.toString() to KanjiVGConverter.toCharacterWritingData(it) }
    println("Done, characters with strokes: ${charactersWritingDataMap.size}")

    println("Searching for common radicals...")
    val standardRadicals = charactersWritingDataMap
        .flatMap { (char, writingData) -> writingData.standardRadicals.map { it to char } }
        .groupBy { (radicalItem, char) -> radicalItem.radical }
        .map { (radical, radicalVariants) ->

            val radicalVariantStrokesCountToListOjKanji = radicalVariants
                .groupBy { (radicalItem, kanji) -> radicalItem.strokesCount }

            if (radicalVariantStrokesCountToListOjKanji.size > 1) {
                val message = radicalVariantStrokesCountToListOjKanji
                    .map { (strokesCount, radicalItemToKanji) ->
                        "$strokesCount strokes in characters[${radicalItemToKanji.joinToString("") { it.second }}]"
                    }
                    .joinToString()
                println("Attention! Radical $radical has multiple variants: $message")
            }

            radicalVariantStrokesCountToListOjKanji
                .maxBy { (strokesCount, radicalItems) -> strokesCount }
                .value
                .first()
                .first
        }
    println("Done, ${standardRadicals.size} radicals found")

    println("Parsing character info (KanjiDic) ...")
    val kanjiDicData = KanjiDicParser.parse(KanjiDicFile)
    println("Done, ${kanjiDicData.size} items found")

    println("Filtering out characters without strokes...")
    val charactersWithInfo = kanjiDicData.asSequence()
        .filter { charactersWritingDataMap.contains(it.character.toString()) }
        .associate { it.character.toString() to KanjiDicEntryConverter.toKanjiInfoData(it) }
    println("Done, characters with info: ${charactersWithInfo.size}")

    println("Parsing dictionary (JMDict) ...")
    val jmDictItems = JMdictParser.parse(JMDictFile)
    // 173 737 - without priorities, 24 859 - with priorities, 22 160 - start with nf
    println("Done, ${jmDictItems.size} items found")

    println("Parsing dictionary furigana (JMDictFurigana) ...")
    val jmDictFuriganaItems = JMdictFuriganaParser.parse(JMDictFuriganaFile).groupBy { it.text }
    println("Done, ${jmDictFuriganaItems.size}")

    println("Looking for expressions with known characters")
    val words = jmDictItems.asSequence()
        .filter { it.priority.isNotEmpty() }
        .map { it to jmDictFuriganaItems[it.expression] }
        .filter { (dictionaryItem, furiganaItems) ->
            furiganaItems != null &&
                    dictionaryItem.expression.any { charactersWritingDataMap.contains(it.toString()) }
        }
        .map { (dictionaryItem, furiganaItems) ->
            WordConverter.convert(dictionaryItem, furiganaItems!!.first())
        }
        .toList()
    println("Done, found ${words.size} words")

    val shouldCreateDB = args.firstOrNull()?.toBoolean() ?: true

    if (!shouldCreateDB) {
        println("Should not create DB, exiting...")
        return
    }

    println("Creating DB...")

    val outputDatabaseFile = File("app/src/main/assets/kanji_data.sqlite")

    if (outputDatabaseFile.exists()) {
        println("Existing DB found, deleting...")
        outputDatabaseFile.delete()
        outputDatabaseFile.createNewFile()
    }

    val database = Database.connect("jdbc:sqlite:${outputDatabaseFile.absolutePath}")
    val exporter = DataExporter(database)

    println("Writing strokes data...")
    exporter.writeKanjiStrokes(
        characterToStrokes = charactersWritingDataMap.mapValues { it.value.strokes }
    )

    println("Writing radicals...")
    exporter.writeRadicals(radicals = standardRadicals)

    println("Writing character radicals...")
    exporter.writeCharacterRadicals(
        data = charactersWritingDataMap.values.flatMap { it.allRadicals }
    )

    println("Writing characters info data...")
    exporter.writeKanjiData(kanjiDataList = charactersWithInfo.values.toList())

    println("Writing characters words data...")
    exporter.writeWords(words = words)
    println("Done")

}
