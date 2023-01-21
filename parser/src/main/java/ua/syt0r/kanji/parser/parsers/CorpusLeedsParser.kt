package ua.syt0r.kanji.parser.parsers

import java.io.File

data class CorpusLeedsItem(
    val wordRank: Int,
    val normalizedFrequency: Float,
    val word: String
)

object CorpusLeedsParser {

    fun parse(file: File): List<CorpusLeedsItem> {

        val allLines = file.readLines()

        val firstDataLineIndex = allLines.indexOfFirst {
            it.startsWith("1") && it.split(" ").size == 3
        }

        return allLines.subList(firstDataLineIndex, allLines.size)
            .map {
                val lineValues = it.split(" ")
                if (lineValues.size != 3) error("Line [$it] is bad")
                CorpusLeedsItem(
                    wordRank = lineValues[0].toInt(),
                    normalizedFrequency = lineValues[1].toFloat(),
                    word = lineValues[2]
                )
            }

    }

}