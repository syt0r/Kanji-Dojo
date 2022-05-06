@file:OptIn(ExperimentalTextApi::class)

package ua.syt0r.kanji.presentation.screen.screen.practice_import.data

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import ua.syt0r.kanji.presentation.common.appendLink

class ImportPracticeCategory(
    val title: String,
    val description: AnnotatedString,
    val items: List<ImportPracticeItem>
)

val kanaImportPracticeCategory = ImportPracticeCategory(
    title = "Kana",
    description = buildAnnotatedString {
        append("Kana is the most basic japanese writing system, which consist of 2 alphabets: hiragana - used for native Japanese words and grammatical elements, and katakana that represents foreign words. ")
        appendLink("Wikipedia. ", "https://en.wikipedia.org/wiki/Kana")
    },
    items = listOf(
        hiraganaImportItem,
        katakanaImportItem
    )
)

val jlptImportPracticeCategory = ImportPracticeCategory(
    title = "JLPT",
    description = buildAnnotatedString {
        append("The Japanese-Language Proficiency Test, or JLPT, is a standardized criterion-referenced test to evaluate and certify Japanese language proficiency for non-native speakers, covering language knowledge, reading ability, and listening ability. ")
        appendLink(
            "Wikipedia. ",
            "https://en.wikipedia.org/wiki/Japanese-Language_Proficiency_Test"
        )
    },
    items = jlptImportItems
)