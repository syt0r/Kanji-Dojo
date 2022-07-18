package ua.syt0r.kanji.presentation.screen.screen.practice_import.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import ua.syt0r.kanji.presentation.common.appendLink

class ImportPracticeCategory(
    val title: String,
    val descriptionBuilder: @Composable () -> AnnotatedString,
    val items: List<ImportPracticeItem>
)

val kanaImportPracticeCategory = ImportPracticeCategory(
    title = "Kana",
    descriptionBuilder = {
        buildAnnotatedString {
            append("Kana is the most basic japanese writing system, which consist of 2 alphabets: hiragana - used for native Japanese words and grammatical elements, and katakana that represents foreign words. ")
            appendLink("More info. ", "https://en.wikipedia.org/wiki/Kana")
        }
    },
    items = listOf(
        hiraganaImportItem,
        katakanaImportItem
    )
)

val jlptImportPracticeCategory = ImportPracticeCategory(
    title = "JLPT",
    descriptionBuilder = {
        buildAnnotatedString {
            append("The Japanese-Language Proficiency Test, or JLPT, is a standardized criterion-referenced test to evaluate and certify Japanese language proficiency for non-native speakers, covering language knowledge, reading ability, and listening ability. ")
            appendLink(
                "More info. ",
                "https://en.wikipedia.org/wiki/Japanese-Language_Proficiency_Test"
            )
        }
    },
    items = jlptImportItems
)

val gradeImportPracticeCategory = ImportPracticeCategory(
    title = "Grade",
    descriptionBuilder = {
        buildAnnotatedString {
            appendLink("The Jōyō kanji", "https://en.wikipedia.org/wiki/J%C5%8Dy%C5%8D_kanji")
            append(" is a list of 2,136 frequently used characters maintained officially by the Japanese Ministry of Education. All these characters are taught in Japanese schools:\n")
            append("• 1,026 kanji taught in primary school (Grade 1-6) (the ")
            appendLink("kyōiku kanji", "https://en.wikipedia.org/wiki/Ky%C5%8Diku_kanji")
            append(")\n")
            append("• 1,110 additional kanji taught in secondary school (Grade 7-12)")
        }
    },
    items = gradeImportItems
)