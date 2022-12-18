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
            },
            priority = when {

                dictionaryItem.priority.any { it.startsWith("nf") } -> {
                    dictionaryItem.priority
                        .find { it.startsWith("nf") }!!
                        .substring(2)
                        .toInt() * 500
                }

                dictionaryItem.priority.contains("news1") -> 11573
                dictionaryItem.priority.contains("news2") -> 22160
                dictionaryItem.priority.contains("ichi1") -> 8508
                dictionaryItem.priority.contains("ichi2") -> 8531
                dictionaryItem.priority.contains("spec1") -> 1317
                dictionaryItem.priority.contains("spec2") -> 2929
                dictionaryItem.priority.contains("gai1") -> 20000
                dictionaryItem.priority.contains("gai2") -> 20000

                else -> Int.MAX_VALUE

            }
        )
    }

}