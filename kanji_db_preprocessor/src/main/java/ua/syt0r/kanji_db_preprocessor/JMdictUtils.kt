package ua.syt0r.kanji_db_preprocessor

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import ua.syt0r.kanji_db_preprocessor.db.KanjiMeanings
import java.io.File

private data class JMdictItem(
    val expression: String,
    val meanings: List<String>
)

fun prepareKanjiMeanings(database: Database) {
    val document = Jsoup.parse(
        File("data/JMdict_e"),
        Charsets.UTF_8.name()
    )

    val entries = document.select("entry")
        .map {
            val kanjiExpression = it.selectFirst("k_ele > keb")?.text()

            if (kanjiExpression == null || kanjiExpression.length > 1)
                return@map null

            JMdictItem(
                expression = kanjiExpression,
                meanings = it.select("gloss")?.map { it.text() } ?: emptyList()
            )
        }
        .filterNotNull()
        .groupBy { it.expression }
        .map {
            JMdictItem(
                expression = it.key,
                meanings = it.value.flatMap { it.meanings }.distinct()
            )
        }

    // entries.forEach { println(it) }

    println("expressions number = ${entries.size}")

    transaction(database) {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(KanjiMeanings)

        entries.forEach { kanjiItem ->

            kanjiItem.meanings.forEach { kanjiMeaning ->
                KanjiMeanings.insert {
                    it[kanji] = kanjiItem.expression
                    it[meaning] = kanjiMeaning
                }
            }

        }
    }

}