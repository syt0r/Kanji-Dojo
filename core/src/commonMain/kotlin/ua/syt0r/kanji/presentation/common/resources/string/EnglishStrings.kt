package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlin.time.Duration

object EnglishStrings : Strings {

    override val appName: String = "Kanji Dojo"

    override val homeTitle: String = "Kanji Dojo"
    override val homeTabDashboard: String = "Practice"
    override val homeTabSearch: String = "Search"
    override val homeTabSettings: String = "Settings"

    override val practiceDashboard = EnglishPracticeDashboardStrings
    override val createPracticeDialog = EnglishCreatePracticeDialogStrings
    override val search: SearchStrings = EnglishSearchStrings
    override val alternativeDialog: AlternativeDialogStrings = EnglishAlternativeDialogStrings
    override val settings: SettingsStrings = EnglishSettingsStrings

    override val aboutTitle: String = "About"
    override val aboutVersion: String = "Version: %s"
    override val aboutDescription: String =
        "Hone your Japanese writing skills with Kanji Dojo. Pick or create your own list of characters to train. Suitable for both complete beginners and advanced learners. All content is absolutely free and more features are coming"
    override val aboutGithub: String = "Github"
    override val aboutGithubDescription: String = "Source code &amp; development"
    override val aboutCreditsTitle: String = "Credits"
    override val aboutLicenseTemplate: String = "License: %s"
    override val aboutLicenseKanjiVgTitle: String = "KaniVG"
    override val aboutLicenseKanjiVgDescription: String =
        "Provides writing strokes, radicals information"
    override val aboutLicenseKanjiDicTitle: String = "Kanji Dic"
    override val aboutLicenseKanjiDicDescription: String =
        "Provides characters info, such as meanings, readings and classifications"
    override val aboutLicenseTanosTitle: String = "Tanos by Jonathan Waller"
    override val aboutLicenseTanosDescription: String = "Provides JLPT classification for kanji"
    override val aboutLicenseJmDictTitle: String = "JMDict"
    override val aboutLicenseJmDictDescription: String =
        "Japanese-Multilingual dictionary, provides expressions"
    override val aboutLicenseJmDictFuriganaTitle: String = "JmdictFurigana"
    override val aboutLicenseJmDictFuriganaDescription: String =
        "Open-source furigana resource to complement the EDICT/Jmdict and ENAMDICT/Jmnedict dictionary files"
    override val aboutLicenseLeedsCorpusTitle: String = "Frequency list by Leeds university"
    override val aboutLicenseLeedsCorpusDescription: String =
        "Words ranking by frequency of usage in internet"
    override val aboutLicenseCCASA3: String = "Creative Commons Attribution-Share Alike 3.0"
    override val aboutLicenseCCASA4: String = "Creative Commons Attribution-Share Alike 4.0"
    override val aboutLicenseCCBY: String = "Creative Commons BY"

}

object EnglishPracticeDashboardStrings : PracticeDashboardStrings {
    override val emptyMessage = { color: Color ->
        buildAnnotatedString {
            append("Click ")
            withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) { append("+") }
            append(" and save practice to start.\nPractices are used to track your progress")
        }
    }
    override val itemTimeMessage: (Duration?) -> String = {
        "Reviewed:" + when {
            it == null -> "Never"
            it.inWholeDays == 1L -> "${it.inWholeDays} day ago"
            it.inWholeDays > 0 -> "${it.inWholeDays} days ago"
            else -> "Today"
        }
    }
    override val analyticsSuggestionMessage: String =
        "Please consider enabling analytics reports. This data will help improve application. It can always be disabled later in settings screen"
    override val analyticsSuggestionAction: String = "Enable"
}

object EnglishCreatePracticeDialogStrings : CreatePracticeDialogStrings {
    override val title: String = "Create practice"
    override val selectMessage: String = "Select (Kana, JLPT, etc.)"
    override val createMessage: String = "Create custom"
}

object EnglishSearchStrings : SearchStrings {
    override val inputHint: String = "Search for character or words"
    override val charactersTitle: (count: Int) -> String = { "Characters ($it)" }
    override val wordsTitle: (count: Int) -> String = { "Expressions ($it)" }
    override val radicalsSheetTitle: String = "Search by radicals"
    override val radicalsFoundCharacters: String = "Found characters"
    override val radicalsEmptyFoundCharacters: String = "Nothing found"
    override val radicalSheetRadicalsSectionTitle: String = "Radicals"
}

object EnglishAlternativeDialogStrings : AlternativeDialogStrings {
    override val title: String = "Alternative expressions"
    override val readingsTitle: String = "Readings"
    override val meaningsTitle: String = "Meanings"
    override val button: String = "Close"
}

object EnglishSettingsStrings : SettingsStrings {
    override val noTranslationLayoutTitle: String = "No translation layout"
    override val noTranslationLayoutMessage: String = "Hides character translations during writing practice"
    override val analyticsTitle: String = "Analytics"
    override val analyticsMessage: String = "Allow sending anonymous data to improve experience"
    override val aboutTitle: String = "About"
}