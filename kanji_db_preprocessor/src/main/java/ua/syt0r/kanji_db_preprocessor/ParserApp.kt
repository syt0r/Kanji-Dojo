package ua.syt0r.kanji_db_preprocessor

import org.jetbrains.exposed.sql.Database
import ua.syt0r.kanji_db_model.isKana
import ua.syt0r.kanji_db_model.isKanji
import ua.syt0r.kanji_db_preprocessor.parsers.KanjiVGUtils
import ua.syt0r.kanji_db_preprocessor.parsers.KanjiumUtils
import java.io.File

fun main(args: Array<String>) {

    val kanjiVGFolder = File("data/kanji-vg/")
    val kanjiumFile = File("data/kanjium_db.sqlite")

    val outputDatabaseFile = File("app/src/main/assets/kanji_data.sqlite")

    val shouldRecreateDatabase = args.firstOrNull()?.toBoolean() ?: true
    println("shouldRecreateDatabase[$shouldRecreateDatabase]")

    if (shouldRecreateDatabase) {
        if (outputDatabaseFile.exists())
            outputDatabaseFile.delete()
        outputDatabaseFile.createNewFile()
    }

    val database = Database.connect("jdbc:sqlite:${outputDatabaseFile.absolutePath}")

    val kanjiVGData = KanjiVGUtils.parseStrokes(kanjiVGFolder)
    val kanjiDictData = KanjiumUtils.getKanjiDictData(kanjiumFile)

    val stokesDataSet = kanjiVGData.filter { it.kanji.isKanji() || it.kanji.isKana() }
        .map { it.kanji }.toSet()
    val kanjiDataSet = kanjiDictData.filter { it.kanji.isKanji() || it.kanji.isKana() }
        .map { it.kanji }.toSet()

    val coveredKanjiSet = stokesDataSet.intersect(kanjiDataSet)
    println("covered kanji count[${coveredKanjiSet.size}]")
    println("uncovered kanji with strokes[${stokesDataSet.minus(kanjiDataSet).joinToString("")}]")

    println("Writing strokes...")
    DataExporter.writeKanjiStrokes(
        database = database,
        kanjiVGDataList = kanjiVGData.filter { stokesDataSet.contains(it.kanji) }
    )

    println("Writing kanji data...")
    DataExporter.writeKanjiData(
        database = database,
        kanjiDataList = kanjiDictData.filter { stokesDataSet.contains(it.kanji) }
    )

}
