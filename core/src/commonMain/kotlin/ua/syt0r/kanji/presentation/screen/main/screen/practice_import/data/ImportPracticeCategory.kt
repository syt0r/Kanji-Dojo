package ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

class ImportPracticeCategory(
    val titleResolver: StringResolveScope<String>,
    val descriptionResolver: StringResolveScope<AnnotatedString>,
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
    titleResolver = { practiceImport.kanaTitle },
    descriptionResolver = { practiceImport.kanaDescription(Color.Blue) },
    items = listOf(HiraganaImportItem, KatakanaImportItem)
)

val jlptImportPracticeCategory = ImportPracticeCategory(
    titleResolver = { practiceImport.jltpTitle },
    descriptionResolver = { practiceImport.jlptDescription(Color.Blue) },
    items = JlptImportItems
)

val gradeImportPracticeCategory = ImportPracticeCategory(
    titleResolver = { practiceImport.gradeTitle },
    descriptionResolver = { practiceImport.gradeDescription(Color.Blue) },
    items = GradeImportItems
)

val wanikaniImportCategory = ImportPracticeCategory(
    titleResolver = { practiceImport.wanikaniTitle },
    descriptionResolver = { practiceImport.wanikaniDescription(Color.Blue) },
    items = WanikaniImportItems
)