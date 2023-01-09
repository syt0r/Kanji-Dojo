package ua.syt0r.kanji.parser.converter

import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.common.db.entity.Radical
import ua.syt0r.kanji.common.svg.SvgCommandParser
import ua.syt0r.kanji.parser.model.CharacterWritingData
import ua.syt0r.kanji.parser.parsers.KanjiVGCharacterData
import ua.syt0r.kanji.parser.parsers.KanjiVGCompound

object KanjiVGConverter {

    fun toCharacterWritingData(kanjiVGCharacterData: KanjiVGCharacterData): CharacterWritingData {
        return CharacterWritingData(
            character = kanjiVGCharacterData.character,
            strokes = kanjiVGCharacterData.compound.getPaths()
                .onEach { SvgCommandParser.parse(it) }, // verification,
            standardRadicals = kanjiVGCharacterData.compound.findStandardRadicals(),
            allRadicals = kanjiVGCharacterData.compound.run {
                this as KanjiVGCompound.Group
                if (radical != null) { // Ignores top level element unless radical
                    findAllRadicals(kanjiVGCharacterData.character.toString())
                } else {
                    childrens.flatMap { it.findAllRadicals(kanjiVGCharacterData.character.toString()) }
                }
            }
        )
    }

    private fun KanjiVGCompound.getPaths(): List<String> {
        return when (this) {
            is KanjiVGCompound.Group -> childrens.flatMap { it.getPaths() }
            is KanjiVGCompound.Path -> listOf(path)
        }
    }

    private fun KanjiVGCompound.findStandardRadicals(): List<Radical> {
        return when (this) {
            is KanjiVGCompound.Group -> {
                val isStandardRadical = element != null &&
                        part == null &&
                        partial != true &&
                        variant != true &&
                        radical?.isNotEmpty() == true

                val radicals = if (isStandardRadical) {
                    listOf(
                        Radical(
                            radical = element!!,
                            strokesCount = endStrokeIndex - startStrokeIndex + 1
                        )
                    )
                } else listOf()

                radicals.plus(childrens.flatMap { it.findStandardRadicals() })
            }

            is KanjiVGCompound.Path -> listOf()
        }
    }

    private fun KanjiVGCompound.findAllRadicals(kanji: String): List<CharacterRadical> {
        return when (this) {
            is KanjiVGCompound.Group -> {
                val isRadical = element != null

                val radicals = if (isRadical) {
                    listOf(
                        CharacterRadical(
                            character = kanji,
                            radical = element!!,
                            startPosition = startStrokeIndex,
                            strokesCount = endStrokeIndex - startStrokeIndex + 1
                        )
                    )
                } else listOf<CharacterRadical>()

                radicals.plus(childrens.flatMap { it.findAllRadicals(kanji) })
            }
            is KanjiVGCompound.Path -> listOf()
        }
    }

}
