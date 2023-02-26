package ua.syt0r.kanji.parser.converter

import ua.syt0r.kanji.parser.model.CharacterInfoData
import ua.syt0r.kanji.parser.parsers.KanjiDicEntry

object KanjiDicEntryConverter {

    fun toKanjiInfoData(entry: KanjiDicEntry) = CharacterInfoData(
        kanji = entry.character,
        meanings = entry.meanings,
        onReadings = entry.onReadings,
        kunReadings = entry.kunReadings,
        frequency = entry.frequency,
        grade = entry.grade
    )

}