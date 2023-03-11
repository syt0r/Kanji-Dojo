package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import kotlin.time.Duration


typealias StringResolveScope <T> = Strings.() -> T

@Composable
fun <T> resolveString(resolveScope: Strings.() -> T): T {
    return LocalStrings.current.resolveScope()
}

val LocalStrings = compositionLocalOf<Strings> { EnglishStrings }

interface Strings {

    val appName: String

    val homeTitle: String
    val homeTabDashboard: String
    val homeTabSearch: String
    val homeTabSettings: String

    val practiceDashboard: PracticeDashboardStrings
    val createPracticeDialog: CreatePracticeDialogStrings

    val search: SearchStrings
    val alternativeDialog: AlternativeDialogStrings

    val settings: SettingsStrings

    val aboutTitle: String
    val aboutVersion: String
    val aboutDescription: String
    val aboutGithub: String
    val aboutGithubDescription: String
    val aboutCreditsTitle: String
    val aboutLicenseTemplate: String
    val aboutLicenseKanjiVgTitle: String
    val aboutLicenseKanjiVgDescription: String
    val aboutLicenseKanjiDicTitle: String
    val aboutLicenseKanjiDicDescription: String
    val aboutLicenseTanosTitle: String
    val aboutLicenseTanosDescription: String
    val aboutLicenseJmDictTitle: String
    val aboutLicenseJmDictDescription: String
    val aboutLicenseJmDictFuriganaTitle: String
    val aboutLicenseJmDictFuriganaDescription: String
    val aboutLicenseLeedsCorpusTitle: String
    val aboutLicenseLeedsCorpusDescription: String
    val aboutLicenseCCASA3: String
    val aboutLicenseCCASA4: String
    val aboutLicenseCCBY: String

}

interface PracticeDashboardStrings {
    val emptyMessage: (Color) -> AnnotatedString
    val itemTimeMessage: (reviewToNowDuration: Duration?) -> String
    val analyticsSuggestionMessage: String
    val analyticsSuggestionAction: String
}

interface CreatePracticeDialogStrings {
    val title: String
    val selectMessage: String
    val createMessage: String
}

interface SearchStrings {
    val inputHint: String
    val charactersTitle: (count: Int) -> String
    val wordsTitle: (count: Int) -> String
    val radicalsSheetTitle: String
    val radicalsFoundCharacters: String
    val radicalsEmptyFoundCharacters: String
    val radicalSheetRadicalsSectionTitle: String
}

interface AlternativeDialogStrings {
    val title: String
    val readingsTitle: String
    val meaningsTitle: String
    val button: String
}

interface SettingsStrings {
    val noTranslationLayoutTitle: String
    val noTranslationLayoutMessage: String
    val analyticsTitle: String
    val analyticsMessage: String
    val aboutTitle: String
}

