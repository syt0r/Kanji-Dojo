package ua.syt0r.kanji.parser

import org.jetbrains.exposed.sql.Database
import ua.syt0r.kanji.parser.converter.KanjiDicEntryConverter
import ua.syt0r.kanji.parser.converter.WordConverter
import ua.syt0r.kanji.parser.model.CharacterStrokesData
import ua.syt0r.kanji.parser.parsers.JMdictFuriganaParser
import ua.syt0r.kanji.parser.parsers.JMdictParser
import ua.syt0r.kanji.parser.parsers.KanjiDicParser
import ua.syt0r.kanji.parser.parsers.KanjiVGParser
import ua.syt0r.kanji.common.isKana
import ua.syt0r.kanji.common.isKanji
import java.io.File

private val DataFolder = File("parser_data/")
private val KanjiVGFolder = File(DataFolder, "kanji-vg/")
private val KanjiDicFile = File("data/kanjidic2.xml")
private val JMDictFile = File(DataFolder, "JMdict_e")
private val JMDictFuriganaFile = File(DataFolder, "JmdictFurigana.json")

fun main(args: Array<String>) {

    println("Parsing character strokes (KanjiVG) ...")
    val kanjiVGData = KanjiVGParser.parse(KanjiVGFolder)
    println("Done, ${kanjiVGData.size} items found")

    println("Filtering kana and kanji...")
    val charactersWithStrokes = kanjiVGData.asSequence()
        .filter { it.character.isKana() || it.character.isKanji() }
        .map { CharacterStrokesData(kanji = it.character, strokes = it.strokes) }
        .associateByTo(LinkedHashMap()) { it.kanji }
    println("Done, characters with strokes: ${charactersWithStrokes.size}")


    println("Parsing character info (KanjiDic) ...")
    val kanjiDicData = KanjiDicParser.parse(KanjiDicFile)
    println("Done, ${kanjiDicData.size} items found")

    println("Filtering out characters without strokes...")
    val characterInfoList = kanjiDicData.asSequence()
        .filter { charactersWithStrokes.contains(it.character) }
        .map { KanjiDicEntryConverter.toKanjiInfoData(it) }
        .toList()
    println("Done, characters with info: ${characterInfoList.size}")

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
                    dictionaryItem.expression.any { charactersWithStrokes.contains(it) }
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

    println("Writing strokes data...")
    DataExporter.writeKanjiStrokes(
        database = database,
        list = charactersWithStrokes.values.toList()
    )

    println("Writing characters info data...")
    DataExporter.writeKanjiData(
        database = database,
        kanjiDataList = characterInfoList
    )

    println("Writing characters words data...")
    DataExporter.writeWords(
        database = database,
        words = words
    )
    println("Done")

}
