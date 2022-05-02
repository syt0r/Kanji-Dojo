package ua.syt0r.kanji.presentation.screen.screen.practice_import.data

import ua.syt0r.kanji_dojo.shared.CharactersClassification

data class ImportPracticeItem(
    val char: Char,
    val title: String,
    val classification: CharactersClassification
)


val hiraganaImportItem = ImportPracticeItem(
    char = 'あ',
    title = "Hiragana",
    classification = CharactersClassification.Kana.HIRAGANA
)

val katakanaImportItem = ImportPracticeItem(
    char = 'ア',
    title = "Katakana",
    classification = CharactersClassification.Kana.KATAKANA
)

private val jlptKanjiPreview = listOf('一', '言', '合', '軍', '及')
val jlptImportItems: List<ImportPracticeItem> = CharactersClassification.JLPT.values()
    .zip(jlptKanjiPreview)
    .map {
        ImportPracticeItem(
            char = it.second,
            title = "JLPT・${it.first.name}",
            classification = it.first
        )
    }