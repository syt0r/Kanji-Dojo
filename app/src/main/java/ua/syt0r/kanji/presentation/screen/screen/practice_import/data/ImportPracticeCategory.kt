package ua.syt0r.kanji.presentation.screen.screen.practice_import.data

class ImportPracticeCategory(
    val title: String,
    val description: String,
    val items: List<ImportPracticeItem>
)

val kanaImportPracticeCategory = ImportPracticeCategory(
    title = "Kana",
    description = "The term kana may refer to a number of syllabaries used to write Japanese phonological units, morae. Wikipedia",
    items = listOf(
        hiraganaImportItem,
        katakanaImportItem
    )
)

val jlptImportPracticeCategory = ImportPracticeCategory(
    title = "JLPT",
    description = "The Japanese-Language Proficiency Test, or JLPT, is a standardized criterion-referenced test to evaluate and certify Japanese language proficiency for non-native speakers, covering language knowledge, reading ability, and listening ability. Wikipedia",
    items = jlptImportItems
)