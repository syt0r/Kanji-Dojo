package ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data

import ua.syt0r.kanji.common.CharactersClassification

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

val gradeImportItems: List<ImportPracticeItem> = listOf(
    ImportPracticeItem('一', "Grade 1", CharactersClassification.Grade.G1),
    ImportPracticeItem('万', "Grade 2", CharactersClassification.Grade.G2),
    ImportPracticeItem('丁', "Grade 3", CharactersClassification.Grade.G3),
    ImportPracticeItem('不', "Grade 4", CharactersClassification.Grade.G4),
    ImportPracticeItem('久', "Grade 5", CharactersClassification.Grade.G5),
    ImportPracticeItem('並', "Grade 6", CharactersClassification.Grade.G6),
    ImportPracticeItem('丈', "Secondary school", CharactersClassification.Grade.G8),
    ImportPracticeItem('丑', "Kanji for use in names (Jinmeiyō)", CharactersClassification.Grade.G9),
    ImportPracticeItem('乘', "Jinmeiyō kanji variants of Jōyō", CharactersClassification.Grade.G10)
)
