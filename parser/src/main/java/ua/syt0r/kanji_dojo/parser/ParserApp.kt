package ua.syt0r.kanji_dojo.parser

import org.jetbrains.exposed.sql.Database
import ua.syt0r.kanji_dojo.parser.model.KanjiInfoData
import ua.syt0r.kanji_dojo.parser.model.KanjiStrokesData
import ua.syt0r.kanji_dojo.parser.parsers.KanjiDicParser
import ua.syt0r.kanji_dojo.parser.parsers.KanjiVGParser
import ua.syt0r.kanji_dojo.shared.isKana
import ua.syt0r.kanji_dojo.shared.isKanji
import java.io.File

fun main(args: Array<String>) {

    val kanjiVGFolder = File("data/kanji-vg/")
    val kanjiumFile = File("data/kanjium_db.sqlite")
    val kanjiDicFile = File("data/kanjidic2.xml")

    val kanjiVGData = KanjiVGParser.parse(kanjiVGFolder)
    val kanjiDicData = KanjiDicParser.parse(kanjiDicFile)

    val strokes = kanjiVGData.map { KanjiStrokesData(kanji = it.kanji, strokes = it.strokes) }
    val kanjiInfoList = kanjiDicData.filter { it.kanji.isKana() || it.kanji.isKanji() }.map {
        KanjiInfoData(
            kanji = it.kanji,
            meanings = it.meanings,
            onReadings = it.onReadings,
            kunReadings = it.kunReadings,
            jlpt = findJLPTForKanji(it.kanji)?.name,
            frequency = it.frequency,
            grade = it.grade
        )
    }


    val outputDatabaseFile = File("app/src/main/assets/kanji_data.sqlite")

    val shouldRecreateDatabase = args.firstOrNull()?.toBoolean() ?: true
    println("shouldRecreateDatabase[$shouldRecreateDatabase]")

    if (shouldRecreateDatabase) {
        if (outputDatabaseFile.exists())
            outputDatabaseFile.delete()
        outputDatabaseFile.createNewFile()
    }

    val database = Database.connect("jdbc:sqlite:${outputDatabaseFile.absolutePath}")

    println("Writing $strokes kanji with strokes...")
    DataExporter.writeKanjiStrokes(
        database = database,
        list = strokes
    )

    println("Writing kanji data...")
    DataExporter.writeKanjiData(
        database = database,
        kanjiDataList = kanjiInfoList
    )

}
