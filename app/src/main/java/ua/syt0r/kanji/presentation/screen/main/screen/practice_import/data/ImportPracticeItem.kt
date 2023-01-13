package ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ua.syt0r.kanji.R
import ua.syt0r.kanji.common.CharactersClassification

data class ImportPracticeItem(
    val previewCharacter: Char,
    val title: @Composable () -> String,
    val classification: CharactersClassification
)


val HiraganaImportItem = ImportPracticeItem(
    previewCharacter = 'あ',
    title = { stringResource(R.string.practice_import_category_kana_hiragana) },
    classification = CharactersClassification.Kana.HIRAGANA
)

val KatakanaImportItem = ImportPracticeItem(
    previewCharacter = 'ア',
    title = { stringResource(R.string.practice_import_category_kana_katakana) },
    classification = CharactersClassification.Kana.KATAKANA
)

private val JlptPreviewKanjiList = listOf('一', '言', '合', '軍', '及')

val JlptImportItems: List<ImportPracticeItem> = CharactersClassification.JLPT.values()
    .zip(JlptPreviewKanjiList)
    .map { (jlpt, previewChar) ->
        ImportPracticeItem(
            previewCharacter = previewChar,
            title = { stringResource(R.string.practice_import_jlpt_item_template, jlpt.name) },
            classification = jlpt
        )
    }

val GradeImportItems: List<ImportPracticeItem> = listOf(
    ImportPracticeItem(
        previewCharacter = '一',
        title = { stringResource(R.string.practice_import_grade_item_template, 1) },
        classification = CharactersClassification.Grade.G1
    ),
    ImportPracticeItem(
        previewCharacter = '万',
        title = { stringResource(R.string.practice_import_grade_item_template, 2) },
        classification = CharactersClassification.Grade.G2
    ),
    ImportPracticeItem(
        previewCharacter = '丁',
        title = { stringResource(R.string.practice_import_grade_item_template, 3) },
        classification = CharactersClassification.Grade.G3
    ),
    ImportPracticeItem(
        previewCharacter = '不',
        title = { stringResource(R.string.practice_import_grade_item_template, 4) },
        classification = CharactersClassification.Grade.G4
    ),
    ImportPracticeItem(
        previewCharacter = '久',
        title = { stringResource(R.string.practice_import_grade_item_template, 5) },
        classification = CharactersClassification.Grade.G5
    ),
    ImportPracticeItem(
        previewCharacter = '並',
        title = { stringResource(R.string.practice_import_grade_item_template, 6) },
        classification = CharactersClassification.Grade.G6
    ),
    ImportPracticeItem(
        previewCharacter = '丈',
        title = { stringResource(R.string.practice_import_grade_8_item) },
        classification = CharactersClassification.Grade.G8
    ),
    ImportPracticeItem(
        previewCharacter = '丑',
        title = { stringResource(R.string.practice_import_grade_9_item) },
        classification = CharactersClassification.Grade.G9
    ),
    ImportPracticeItem(
        previewCharacter = '乘',
        title = { stringResource(R.string.practice_import_grade_10_item) },
        classification = CharactersClassification.Grade.G10
    )
)
