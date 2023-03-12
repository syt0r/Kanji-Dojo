package ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.presentation.common.resources.string.resolveString

data class ImportPracticeItem(
    val previewCharacter: Char,
    val title: @Composable () -> String,
    val classification: CharactersClassification
)


val HiraganaImportItem = ImportPracticeItem(
    previewCharacter = 'あ',
    title = { resolveString { practiceImport.hiragana } },
    classification = CharactersClassification.Kana.Hiragana
)

val KatakanaImportItem = ImportPracticeItem(
    previewCharacter = 'ア',
    title = { resolveString { practiceImport.katakana } },
    classification = CharactersClassification.Kana.Katakana
)

private val JlptPreviewKanjiList = listOf('一', '言', '合', '軍', '及')
val JlptImportItems: List<ImportPracticeItem> = CharactersClassification.JLPT.all
    .zip(JlptPreviewKanjiList)
    .map { (jlpt, previewChar) ->
        ImportPracticeItem(
            previewCharacter = previewChar,
            title = { resolveString { practiceImport.jlptItem(jlpt.level) } },
            classification = jlpt
        )
    }

private val GradePreviewKanji = "一万丁不久並丈丑乘".toList()
val GradeImportItems: List<ImportPracticeItem> = CharactersClassification.Grade.all
    .zip(GradePreviewKanji)
    .map { (grade, char) ->
        ImportPracticeItem(
            previewCharacter = char,
            title = { resolveString { practiceImport.getGradeItem(grade.number) } },
            classification = grade
        )
    }

private val WanikaniPreviewKanji = "上玉矢竹角全辺答受進功悪皆能紀浴是告得裕責援演庁慣接怒攻略更帯酸灰豆熊諾患伴控拉棄析襲刃頃墨幣遂概偶又祥諭庶累匠盲陪亜煩"
    .toList()
val WanikaniImportItems: List<ImportPracticeItem> = CharactersClassification.Wanikani.all
    .zip(WanikaniPreviewKanji)
    .map { (classification, char) ->
        ImportPracticeItem(
            previewCharacter = char,
            title = { resolveString { practiceImport.wanikaniItem(classification.level) } },
            classification = classification
        )
    }
