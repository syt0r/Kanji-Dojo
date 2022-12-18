package ua.syt0r.kanji_dojo.parser

import org.jetbrains.exposed.sql.Database
import ua.syt0r.kanji_dojo.parser.converter.KanjiDicEntryConverter
import ua.syt0r.kanji_dojo.parser.converter.WordConverter
import ua.syt0r.kanji_dojo.parser.model.CharacterStrokesData
import ua.syt0r.kanji_dojo.parser.parsers.JMdictFuriganaParser
import ua.syt0r.kanji_dojo.parser.parsers.JMdictParser
import ua.syt0r.kanji_dojo.parser.parsers.KanjiDicParser
import ua.syt0r.kanji_dojo.parser.parsers.KanjiVGParser
import ua.syt0r.kanji_dojo.shared.isKana
import ua.syt0r.kanji_dojo.shared.isKanji
import java.io.File

fun main(args: Array<String>) {

    val kanjiVGFolder = File("data/kanji-vg/")
    val kanjiumFile = File("data/kanjium_db.sqlite")
    val kanjiDicFile = File("data/kanjidic2.xml")

    println("Parsing character strokes (KanjiVG) ...")
    val kanjiVGData = KanjiVGParser.parse(kanjiVGFolder)
    println("Done, ${kanjiVGData.size} items found")

    println("Filtering kana and kanji...")
    val charactersWithStrokes = kanjiVGData.asSequence()
        .filter { it.character.isKana() || it.character.isKanji() }
        .map { CharacterStrokesData(kanji = it.character, strokes = it.strokes) }
        .associateByTo(LinkedHashMap()) { it.kanji }
    println("Done, characters with strokes: ${charactersWithStrokes.size}")


    println("Parsing character info (KanjiDic) ...")
    val kanjiDicData = KanjiDicParser.parse(kanjiDicFile)
    println("Done, ${kanjiDicData.size} items found")

    println("Filtering out characters without strokes...")
    val characterInfoList = kanjiDicData.asSequence()
        .filter { charactersWithStrokes.contains(it.character) }
        .map { KanjiDicEntryConverter.toKanjiInfoData(it) }
        .toList()
    println("Done, characters with info: ${characterInfoList.size}")

    println("Parsing dictionary (JMDict) ...")
    val jmDictItems = JMdictParser.parse()
    // 173 737 - without priorities, 24 859 - with priorities, 22 160 - start with nf
    println("Done, ${jmDictItems.size} items found")

    println("Parsing dictionary furigana (JMDictFurigana) ...")
    val jmDictFuriganaItems = JMdictFuriganaParser.parse().associateBy { it.text }
    println("Done, ${jmDictFuriganaItems.size}")

    println("Looking for expressions with known characters")
    val words = jmDictItems.asSequence()
        .filter { it.priority.isNotEmpty() }
        .map { it to jmDictFuriganaItems[it.expression] }
        .filter { (dictionaryItem, furiganaItem) ->
            furiganaItem != null &&
                    dictionaryItem.expression.any { charactersWithStrokes.contains(it) }
        }
        .map { (dictionaryItem, furiganaItem) ->
            WordConverter.convert(dictionaryItem, furiganaItem!!)
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
