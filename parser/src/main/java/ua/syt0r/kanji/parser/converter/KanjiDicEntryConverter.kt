package ua.syt0r.kanji.parser.converter

import ua.syt0r.kanji.parser.findJLPTForKanji
import ua.syt0r.kanji.parser.model.CharacterInfoData
import ua.syt0r.kanji.parser.parsers.KanjiDicEntry

object KanjiDicEntryConverter {

    fun toKanjiInfoData(entry: KanjiDicEntry) = CharacterInfoData(
        kanji = entry.character,
        meanings = entry.meanings,
        onReadings = entry.onReadings,
        kunReadings = entry.kunReadings,
        jlpt = findJLPTForKanji(entry.character)?.name,
        frequency = entry.frequency,
        grade = entry.grade
    )

}