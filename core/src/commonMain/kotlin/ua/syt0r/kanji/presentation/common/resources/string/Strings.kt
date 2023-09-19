package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.LocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration


typealias StringResolveScope <T> = Strings.() -> T

@Composable
fun <T> resolveString(resolveScope: Strings.() -> T): T {
    return LocalStrings.current.resolveScope()
}

fun getStrings(): Strings {
    return when (Locale.current.language) {
        "ja" -> JapaneseStrings
        else -> EnglishStrings
    }
}

val LocalStrings = compositionLocalOf<Strings> { EnglishStrings }

interface Strings {

    val appName: String

    val hiragana: String
    val katakana: String

    val kunyomi: String
    val onyomi: String

    val homeTitle: String
    val homeTabDashboard: String
    val homeTabSearch: String
    val homeTabSettings: String

    val practiceDashboard: PracticeDashboardStrings
    val createPracticeDialog: CreatePracticeDialogStrings
    val dailyGoalDialog: DailyGoalDialogStrings

    val search: SearchStrings
    val alternativeDialog: AlternativeDialogStrings

    val settings: SettingsStrings
    val about: AboutStrings

    val practiceImport: PracticeImportStrings
    val practiceCreate: PracticeCreateStrings
    val practicePreview: PracticePreviewStrings

    val writingPractice: WritingPracticeStrings
    val readingPractice: ReadingPracticeStrings

    val kanjiInfo: KanjiInfoStrings

    val urlPickerMessage: String
    val urlPickerErrorMessage: String

}

interface PracticeDashboardStrings {

    val emptyScreenMessage: (Color) -> AnnotatedString

    val itemTimeMessage: (reviewToNowDuration: Duration?) -> String
    val itemWritingTitle: String
    val itemReadingTitle: String
    val itemTotal: String
    val itemDone: String
    val itemReview: String
    val itemNew: String
    val itemQuickPracticeTitle: String
    val itemQuickPracticeLearn: (Int) -> String
    val itemQuickPracticeReview: (Int) -> String
    val itemGraphProgressTitle: String
    val itemGraphProgressValue: (Float) -> String
        get() = { " ${it.roundToInt()}%" }

    val dailyIndicatorPrefix: String
    val dailyIndicatorCompleted: String
    val dailyIndicatorNew: (Int) -> String
    val dailyIndicatorReview: (Int) -> String

}

interface CreatePracticeDialogStrings {
    val title: String
    val selectMessage: String
    val createMessage: String
}

interface DailyGoalDialogStrings {
    val title: String
    val message: String
    val studyLabel: String
    val reviewLabel: String
    val noteMessage: String
    val applyButton: String
    val cancelButton: String
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
    val leftHandedModeTitle: String
    val leftHandedModeMessage: String
    val analyticsTitle: String
    val analyticsMessage: String
    val themeTitle: String
    val themeSystem: String
    val themeLight: String
    val themeDark: String
    val aboutTitle: String
}

interface AboutStrings {
    val title: String
    val version: (versionName: String) -> String
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

interface PracticeImportStrings {

    val title: String

    val kanaTitle: String
    val kanaDescription: (urlColor: Color) -> AnnotatedString
    val hiragana: String
    val katakana: String

    val jltpTitle: String
    val jlptDescription: (urlColor: Color) -> AnnotatedString
    val jlptItem: (level: Int) -> String

    val gradeTitle: String
    val gradeDescription: (urlColor: Color) -> AnnotatedString

    fun getGradeItem(grade: Int): String {
        return when {
            grade <= 6 -> gradeItemNumbered(grade)
            grade == 8 -> gradeItemSecondary
            grade == 9 -> gradeItemNames
            grade == 10 -> gradeItemNamesVariants
            else -> throw IllegalStateException("Unexpected grade $grade")
        }
    }

    val gradeItemNumbered: (Int) -> String
    val gradeItemSecondary: String
    val gradeItemNames: String
    val gradeItemNamesVariants: String

    val wanikaniTitle: String
    val wanikaniDescription: (urlColor: Color) -> AnnotatedString
    val wanikaniItem: (Int) -> String

}

interface PracticeCreateStrings {
    val newTitle: String
    val ediTitle: String

    val searchHint: String

    val infoAction: String
    val returnAction: String
    val removeAction: String

    val saveTitle: String
    val saveInputHint: String
    val saveButtonDefault: String
    val saveButtonCompleted: String

    val deleteTitle: String
    val deleteMessage: (practiceTitle: String) -> String
    val deleteButtonDefault: String
    val deleteButtonCompleted: String

    val unknownTitle: String
    val unknownMessage: (characters: List<String>) -> String
    val unknownButton: String
}

interface PracticePreviewStrings {

    val emptyListMessage: String
    fun listGroupTitle(index: Int, characters: String): String = "$index. $characters"

    val detailsGroupTitle: (index: Int) -> String
    val reviewStateRecently: String
    val reviewStateNeedReview: String
    val reviewStateNever: String

    val firstTimeReviewMessage: (LocalDateTime?) -> String
    val lastTimeReviewMessage: (LocalDateTime?) -> String

    val groupDetailsDateTimeFormatter: (LocalDateTime) -> String
        get() = {
            it.run { "${dayOfMonth.withLeading0}/${monthNumber.withLeading0}/$year ${hour.withLeading0}:${minute.withLeading0}" }
        }

    val detailsConfigStudy: String
    val detailsConfigReview: String
    val detailsConfigShuffle: String
    val detailsConfigNoShuffle: String
    val detailsPracticeButton: String

    val practiceTypeWriting: String
    val practiceTypeReading: String
    val filterAll: String
    val filterReviewOnly: String
    val filterNewOnly: String
    val sortOptionAddOrder: String
    val sortOptionAddOrderHint: String
    val sortOptionFrequency: String
    val sortOptionFrequencyHint: String
    val sortOptionName: String
    val sortOptionNameHint: String

    val screenConfigDialog: PracticePreviewScreenConfigDialogStrings
    val studyOptionsDialog: PracticePreviewStudyOptionsDialogStrings

    val multiselectTitle: (selectedCount: Int) -> String
    val multiselectDataNotLoaded: String
    val multiselectNoSelected: String
    val multiselectDialog: MultiselectDialogStrings

}

interface PracticePreviewScreenConfigDialogStrings {
    val title: String
    val practiceType: String
    val filter: String
    val sorting: String
    val buttonCancel: String
    val buttonApply: String
}

interface PracticePreviewStudyOptionsDialogStrings {
    val title: String
    val studyMode: String
    val shuffle: String
    val button: String
}

interface MultiselectDialogStrings {
    val title: String
    val message: String
    val selected: String
    val button: String
}

interface WritingPracticeStrings {
    val headerWordsMessage: (count: Int) -> String
    val wordsBottomSheetTitle: String
    val nextButton: String
    val repeatButton: String

    val leaveDialogTitle: String
    val leaveDialogMessage: String
    val leaveDialogButton: String

    val savingTitle: String
    val savingPreselectTitle: String
    val savingPreselectCount: (Int) -> String
    val savingMistakesMessage: (count: Int) -> String
    val savingButton: String

    val savedTitle: String
    val savedReviewedCountLabel: String
    val savedTimeSpentLabel: String
    val savedAccuracyLabel: String
    val savedRepeatCharactersLabel: String
    val savedRetainedCharactersLabel: String
    val savedButton: String
}

interface ReadingPracticeStrings {
    val words: String
    val showAnswerButton: String
    val goodButton: String
    val repeatButton: String
    val summaryMistakesMessage: (count: Int) -> String
    val summaryButton: String
}

interface KanjiInfoStrings {
    val strokesMessage: (count: Int) -> AnnotatedString
    val clipboardCopyMessage: String
    val radicalsSectionTitle: (count: Int) -> String
    val noRadicalsMessage: String
    val wordsSectionTitle: (count: Int) -> String
    val romajiMessage: (romaji: String) -> String
    val gradeMessage: (grade: Int) -> String
    val jlptMessage: (level: Int) -> String
    val frequencyMessage: (frequency: Int) -> String
    val noDataMessage: String
}

