package ua.syt0r.kanji_db_preprocessor

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import ua.syt0r.kanji_db_model.model.KanjiReadingData
import ua.syt0r.kanji_db_model.db.ReadingsTableConstants
import ua.syt0r.kanji_db_preprocessor.db.KanjiReadings
import ua.syt0r.kanji_db_preprocessor.db.KanjiStrokes
import java.io.File

fun prepareKanjiReadings(database: Database) {

    val document = Jsoup.parse(
        File("data/kanjidic2.xml"),
        Charsets.UTF_8.name()
    )

    val data = document.select("character")
        .map {

            val readingElements = it.select("reading")

            val kunReadings = readingElements
                .filter { it.attr("r_type").startsWith("ja_kun") }
                .map { it.text() }

            val onReadings = readingElements
                .filter { it.attr("r_type").startsWith("ja_on") }
                .map { it.text() }

            KanjiDicData(
                kanji = it.selectFirst("literal").text().first(),
                kunReadings = kunReadings,
                onReadings = onReadings
            )
        }

    transaction(database) {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(KanjiReadings)

        data
            .filter {
                KanjiStrokes.select { KanjiStrokes.kanji eq it.kanji.toString() }
                    .empty()
                    .not()
                    .apply { if (!this) println("strokes for kanji[${it.kanji}] not found, skip") }
            }
            .forEach { kanjiData ->

                val readings = kanjiData.kunReadings.map {
                    ReadingsTableConstants.ReadingType.KUN to it
                } + kanjiData.onReadings.map {
                    ReadingsTableConstants.ReadingType.ON to it
                }


                readings.forEach { readingData ->
                    KanjiReadings.insertIgnore {
                        it[kanji] = kanjiData.kanji.toString()
                        it[readingType] = readingData.first.value
                        it[reading] = readingData.second
                    }
                }

            }
    }

}

data class KanjiDicData(
    override val kanji: Char,
    override val onReadings: List<String>,
    override val kunReadings: List<String>
) : KanjiReadingData