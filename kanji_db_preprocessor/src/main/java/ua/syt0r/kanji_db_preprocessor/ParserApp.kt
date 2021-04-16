package ua.syt0r.kanji_db_preprocessor

import org.jetbrains.exposed.sql.Database
import java.io.File

fun main(args: Array<String>) {

    val kanjiStrokesDb = File("app/src/main/assets/kanji-db.sqlite")
    if (kanjiStrokesDb.exists())
        kanjiStrokesDb.delete()
    kanjiStrokesDb.createNewFile()
    print(kanjiStrokesDb.absolutePath)

    val database = Database.connect("jdbc:sqlite:${kanjiStrokesDb.absolutePath}")

    prepareKanjiStrokes(database)
    prepareKanjiReadings(database)
    prepareKanjiMeanings(database)

}
