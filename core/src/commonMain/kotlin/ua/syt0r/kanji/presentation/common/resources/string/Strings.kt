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
    val about: AboutStrings

    val urlPickerMessage: String
    val urlPickerErrorMessage: String

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

interface AboutStrings {
    val title: String
    val version: String
    val description: String
    val githubTitle: String
    val githubDescription: String
    val creditsTitle: String
    val licenseTemplate: (licenseText: String) -> String
    val licenseKanjiVgTitle: String
    val licenseKanjiVgDescription: String
    val licenseKanjiDicTitle: String
    val licenseKanjiDicDescription: String
    val licenseTanosTitle: String
    val licenseTanosDescription: String
    val licenseJmDictTitle: String
    val licenseJmDictDescription: String
    val licenseJmDictFuriganaTitle: String
    val licenseJmDictFuriganaDescription: String
    val licenseLeedsCorpusTitle: String
    val licenseLeedsCorpusDescription: String
    val licenseCCASA3: String
    val licenseCCASA4: String
    val licenseCCBY: String
}

