package ua.syt0r.kanji_dojo.parser.converter

import ua.syt0r.kanji_dojo.parser.findJLPTForKanji
import ua.syt0r.kanji_dojo.parser.model.CharacterInfoData
import ua.syt0r.kanji_dojo.parser.parsers.KanjiDicEntry

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