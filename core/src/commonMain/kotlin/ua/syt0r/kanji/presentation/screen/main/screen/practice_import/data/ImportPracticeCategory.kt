package ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme

sealed interface ImportPracticeCategory {

    val titleResolver: StringResolveScope<String>

    @get:Composable
    val descriptionResolver: StringResolveScope<AnnotatedString>

    val items: List<ImportPracticeItem>

}


object KanaImportPracticeCategory : ImportPracticeCategory {

    override val titleResolver: StringResolveScope<String> = { practiceImport.kanaTitle }

    override val descriptionResolver: StringResolveScope<AnnotatedString>
        @Composable
        get() {
            val color = MaterialTheme.extraColorScheme.link
            return { practiceImport.kanaDescription(color) }
        }

    override val items: List<ImportPracticeItem> = listOf(HiraganaImportItem, KatakanaImportItem)

}

object JlptImportPracticeCategory : ImportPracticeCategory {

    override val titleResolver: StringResolveScope<String> = { practiceImport.jltpTitle }

    override val descriptionResolver: StringResolveScope<AnnotatedString>
        @Composable
        get() {
            val color = MaterialTheme.extraColorScheme.link
            return { practiceImport.jlptDescription(color) }
        }

    override val items: List<ImportPracticeItem> = JlptImportItems

}

object GradeImportPracticeCategory : ImportPracticeCategory {

    override val titleResolver: StringResolveScope<String> = { practiceImport.gradeTitle }

    override val descriptionResolver: StringResolveScope<AnnotatedString>
        @Composable
        get() {
            val color = MaterialTheme.extraColorScheme.link
            return { practiceImport.gradeDescription(color) }
        }

    override val items: List<ImportPracticeItem> = GradeImportItems

}

object WanikaniImportCategory : ImportPracticeCategory {

    override val titleResolver: StringResolveScope<String> = { practiceImport.wanikaniTitle }

    override val descriptionResolver: StringResolveScope<AnnotatedString>
        @Composable
        get() {
            val color = MaterialTheme.extraColorScheme.link
            return { practiceImport.wanikaniDescription(color) }
        }

    override val items: List<ImportPracticeItem> = WanikaniImportItems

}

val AllImportCategories = listOf(
    KanaImportPracticeCategory,
    JlptImportPracticeCategory,
    GradeImportPracticeCategory,
    WanikaniImportCategory
)