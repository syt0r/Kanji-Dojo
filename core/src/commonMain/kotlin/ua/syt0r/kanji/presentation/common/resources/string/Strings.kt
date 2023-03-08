package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf


typealias StringResolveScope = Strings.() -> String

@Composable
fun resolveString(resolveScope: StringResolveScope): String {
    return LocalStrings.current.resolveScope()
}

val LocalStrings = compositionLocalOf<Strings> { EnglishStrings }

interface Strings {

    val appName: String

    val homeTitle: String
    val homeTabDashboard: String
    val homeTabSearch: String
    val homeTabSettings: String

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

