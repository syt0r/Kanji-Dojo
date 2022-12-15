package ua.syt0r.kanji_dojo.parser.converter

import ua.syt0r.kanji_dojo.parser.model.Word
import ua.syt0r.kanji_dojo.parser.parsers.JMdictFuriganaItem
import ua.syt0r.kanji_dojo.parser.parsers.JMdictItem
import ua.syt0r.kanji_dojo.shared.db.FuriganaDBEntity

object WordConverter {

    fun convert(dictionaryItem: JMdictItem, furiganaItem: JMdictFuriganaItem): Word {
        return Word(
            expression = dictionaryItem.expression,
            meanings = dictionaryItem.meanings,
            furigana = furiganaItem.items.map {
                FuriganaDBEntity(
                    text = it.ruby,
                    annotation = it.rt
                )
            }
        )
    }

}