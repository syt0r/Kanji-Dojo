package ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data

import androidx.annotation.StringRes
import ua.syt0r.kanji.R

class ImportPracticeCategory(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val items: List<ImportPracticeItem>
) {

    companion object {
        val all: List<ImportPracticeCategory>
            get() = listOf(
                kanaImportPracticeCategory,
                jlptImportPracticeCategory,
                gradeImportPracticeCategory,
                wanikaniImportCategory
            )
    }

}

val kanaImportPracticeCategory = ImportPracticeCategory(
    title = R.string.practice_import_category_kana_title,
    description = R.string.practice_import_category_kana_description,
    items = listOf(HiraganaImportItem, KatakanaImportItem)
)

val jlptImportPracticeCategory = ImportPracticeCategory(
    title = R.string.practice_import_category_jlpt_title,
    description = R.string.practice_import_category_jlpt_description,
    items = JlptImportItems
)

val gradeImportPracticeCategory = ImportPracticeCategory(
    title = R.string.practice_import_category_grade_title,
    description = R.string.practice_import_category_grade_description,
    items = GradeImportItems
)

val wanikaniImportCategory = ImportPracticeCategory(
    title = R.string.practice_import_category_wanikani_title,
    description = R.string.practice_import_category_wanikani_description,
    items = WanikaniImportItems
)