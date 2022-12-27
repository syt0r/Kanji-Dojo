package ua.syt0r.kanji.parser.parsers

import java.io.File
import java.io.FileReader
import java.nio.charset.Charset

data class Edict2Item(
    val word: String,
    val furigana: String,
    val meaning: String
)

object EdictParser {

    private const val Sample = "遺言信託 [ゆいごんしんたく] /(n) testamentary trust/EntL2634890/"

    fun parse(): List<Edict2Item> {
        val file = File("data/edict2.txt")

        val reader = FileReader(file, Charset.forName("EUC-JP"))
        val lines = reader.readLines()

        println("Detected ${lines.size} lines")

        return lines.asSequence()
            .map { it.split("/") }
            .map {
                try {
                    // TODO
                    Edict2Item(
                        word = it[0],
                        furigana = it[1],
                        meaning = it[1]
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            .filterNotNull()
            .toList()
    }

}