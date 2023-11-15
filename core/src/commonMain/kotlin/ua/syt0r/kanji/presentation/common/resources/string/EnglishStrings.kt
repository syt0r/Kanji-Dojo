package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.presentation.common.withClickableUrl
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import kotlin.time.Duration

object EnglishStrings : Strings {

    override val appName: String = "Kanji Dojo"

    override val hiragana: String = "Hiragana"
    override val katakana: String = "Katakana"

    override val kunyomi: String = "Kun"
    override val onyomi: String = "On"

    override val homeTitle: String = "Kanji Dojo"
    override val homeTabDashboard: String = "Practice"
    override val homeTabSearch: String = "Search"
    override val homeTabSettings: String = "Settings"

    override val practiceDashboard = EnglishPracticeDashboardStrings
    override val createPracticeDialog = EnglishCreatePracticeDialogStrings
    override val dailyGoalDialog: DailyGoalDialogStrings = EnglishDailyGoalDialogStrings
    override val search: SearchStrings = EnglishSearchStrings
    override val alternativeDialog: AlternativeDialogStrings = EnglishAlternativeDialogStrings
    override val settings: SettingsStrings = EnglishSettingsStrings
    override val reminderDialog: ReminderDialogStrings = EnglishReminderDialogStrings
    override val about: AboutStrings = EnglishAboutStrings
    override val practiceImport: PracticeImportStrings = EnglishPracticeImportStrings
    override val practiceCreate: PracticeCreateStrings = EnglishPracticeCreateStrings
    override val practicePreview: PracticePreviewStrings = EnglishPracticePreviewStrings
    override val commonPractice: CommonPracticeStrings = EnglishCommonPracticeStrings
    override val writingPractice: WritingPracticeStrings = EnglishWritingPracticeStrings
    override val readingPractice: ReadingPracticeStrings = EnglishReadingPracticeString
    override val kanjiInfo: KanjiInfoStrings = EnglishKanjiInfoStrings

    override val urlPickerMessage: String = "Open With"
    override val urlPickerErrorMessage: String = "Web browser not found"

    override val reminderNotification: ReminderNotificationStrings =
        EnglishReminderNotificationStrings

}

object EnglishPracticeDashboardStrings : PracticeDashboardStrings {
    override val emptyScreenMessage = { color: Color ->
        buildAnnotatedString {
            append("Click ")
            withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) { append("+") }
            append(" and save practice to start.\nPractices are used to track your progress")
        }
    }
    override val itemTimeMessage: (Duration?) -> String = {
        "Reviewed: " + when {
            it == null -> "Never"
            it.inWholeDays == 1L -> "${it.inWholeDays} day ago"
            it.inWholeDays > 0 -> "${it.inWholeDays} days ago"
            else -> "Less than a day ago"
        }
    }
    override val itemWritingTitle: String = "Writing"
    override val itemReadingTitle: String = "Reading"
    override val itemTotal: String = "Total"
    override val itemDone: String = "Done"
    override val itemReview: String = "Due"
    override val itemNew: String = "New"
    override val itemQuickPracticeTitle: String = "Quick practice"
    override val itemQuickPracticeLearn: (Int) -> String = { "Learn new ($it)" }
    override val itemQuickPracticeReview: (Int) -> String = { "Review ($it)" }
    override val itemGraphProgressTitle: String = "Completion"

    override val dailyIndicatorPrefix: String = "Daily goal: "
    override val dailyIndicatorCompleted: String = "Completed"
    override val dailyIndicatorNew: (Int) -> String = { "$it new" }
    override val dailyIndicatorReview: (Int) -> String = { "$it review" }
}

object EnglishCreatePracticeDialogStrings : CreatePracticeDialogStrings {
    override val title: String = "Create practice"
    override val selectMessage: String = "Select (Kana, JLPT, etc.)"
    override val createMessage: String = "Create custom"
}

object EnglishDailyGoalDialogStrings : DailyGoalDialogStrings {
    override val title: String = "Daily Limit"
    override val message: String =
        "Enable to limit count of characters for quick practice and reminder notification appearance"
    override val studyLabel: String = "Study"
    override val reviewLabel: String = "Review"
    override val noteMessage: String =
        "Note: Writing and reading reviews are counted separately towards the goal"
    override val applyButton: String = "Apply"
    override val cancelButton: String = "Cancel"
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
    override val analyticsTitle: String = "Analytics"
    override val analyticsMessage: String = "Allow sending anonymous data to improve experience"
    override val themeTitle: String = "Theme"
    override val themeSystem: String = "System"
    override val themeLight: String = "Light"
    override val themeDark: String = "Dark"
    override val reminderTitle: String = "Reminder Notification"
    override val reminderEnabled: String = "Enabled"
    override val reminderDisabled: String = "Disabled"
    override val aboutTitle: String = "About"
}

object EnglishReminderDialogStrings : ReminderDialogStrings {
    override val title: String = "Reminder Notification"
    override val noPermissionLabel: String = "Missing notification permission"
    override val noPermissionButton: String = "Grant"
    override val enabledLabel: String = "Enabled"
    override val timeLabel: String = "Time"
    override val cancelButton: String = "Close"
    override val applyButton: String = "Apply"
}

object EnglishAboutStrings : AboutStrings {

    override val title: String = "About"
    override val version: (versionName: String) -> String = { "Version: $it" }
    override val description: String =
        "Hone your Japanese writing skills with Kanji Dojo. Pick or create your own list of characters to train. Suitable for both complete beginners and advanced learners. All content is absolutely free and more features are coming"
    override val githubTitle: String = "Project's Github Page"
    override val githubDescription: String = "Source code, bug reports, discussions"
    override val creditsTitle: String = "Credits"
    override val licenseTemplate: (String) -> String = { "License: $it" }
    override val licenseKanjiVgTitle: String = "KaniVG"
    override val licenseKanjiVgDescription: String =
        "Provides writing strokes, radicals information"
    override val licenseKanjiDicTitle: String = "Kanji Dic"
    override val licenseKanjiDicDescription: String =
        "Provides characters info, such as meanings, readings and classifications"
    override val licenseTanosTitle: String = "Tanos by Jonathan Waller"
    override val licenseTanosDescription: String = "Provides JLPT classification for kanji"
    override val licenseJmDictTitle: String = "JMDict"
    override val licenseJmDictDescription: String =
        "Japanese-Multilingual dictionary, provides expressions"
    override val licenseJmDictFuriganaTitle: String = "JmdictFurigana"
    override val licenseJmDictFuriganaDescription: String =
        "Open-source furigana resource to complement the EDICT/Jmdict and ENAMDICT/Jmnedict dictionary files"
    override val licenseLeedsCorpusTitle: String = "Frequency list by Leeds university"
    override val licenseLeedsCorpusDescription: String =
        "Words ranking by frequency of usage in internet"
    override val licenseCCASA3: String = "Creative Commons Attribution-Share Alike 3.0"
    override val licenseCCASA4: String = "Creative Commons Attribution-Share Alike 4.0"
    override val licenseCCBY: String = "Creative Commons BY"

}

object EnglishPracticeImportStrings : PracticeImportStrings {

    override val title: String = "Select"

    override val kanaTitle: String = "Kana"

    override val kanaDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("Kana is the most basic japanese writing system, which consist of 2 alphabets: hiragana - used for native Japanese words and grammatical elements, and katakana that represents foreign words. ")
            withClickableUrl(
                url = "https://en.wikipedia.org/wiki/Kana",
                color = urlColor
            ) {
                append("More info.")
            }
        }
    }
    override val hiragana: String = "Hiragana"
    override val katakana: String = "Katakana"

    override val jltpTitle: String = "JLPT"
    override val jlptDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("The Japanese-Language Proficiency Test, or JLPT, is a standardized criterion-referenced test to evaluate and certify Japanese language proficiency for non-native speakers, covering language knowledge, reading ability, and listening ability. ")
            withClickableUrl(
                url = "https://en.wikipedia.org/wiki/Japanese-Language_Proficiency_Test",
                color = urlColor
            ) {
                append("More info.")
            }
        }
    }
    override val jlptItem: (level: Int) -> String = { "JLPT・N$it" }

    override val gradeTitle: String = "Grade"
    override val gradeDescription = { urlColor: Color ->
        buildAnnotatedString {
            withClickableUrl("https://en.wikipedia.org/wiki/J%C5%8Dy%C5%8D_kanji", urlColor) {
                append("The Jōyō kanji")
            }
            append(" is a list of 2,136 frequently used characters maintained officially by the Japanese Ministry of Education. ")
            append("All these characters are taught in Japanese schools:\n")
            append(" • 1,026 kanji taught in primary school (Grade 1-6) (the ")
            withClickableUrl("https://en.wikipedia.org/wiki/Ky%C5%8Diku_kanji", urlColor) {
                append("kyōiku kanji")
            }
            append(")\n")
            append(" • 1,110 additional kanji taught in secondary school (Grade 7-12)")
        }
    }
    override val gradeItemNumbered: (Int) -> String = { "Grade $it" }
    override val gradeItemSecondary: String = "Secondary school"
    override val gradeItemNames: String = "Kanji for use in names (Jinmeiyō)"
    override val gradeItemNamesVariants: String = "Jinmeiyō kanji variants of Jōyō"

    override val wanikaniTitle: String = "Wanikani"
    override val wanikaniDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("Kanji lists according to levels on website Wanikani by Tofugu. ")
            withClickableUrl("https://www.wanikani.com/kanji?difficulty=pleasant", urlColor) {
                append("More info. ")
            }
        }
    }
    override val wanikaniItem: (Int) -> String = { "Wanikani Level $it" }

}

object EnglishPracticeCreateStrings : PracticeCreateStrings {
    override val newTitle: String = "Create"
    override val ediTitle: String = "Edit"
    override val searchHint: String = "Enter kana or kanji"
    override val infoAction: String = "Info"
    override val returnAction: String = "Return"
    override val removeAction: String = "Remove"
    override val saveTitle: String = "Save changes"
    override val saveInputHint: String = "Practice Title"
    override val saveButtonDefault: String = "Save"
    override val saveButtonCompleted: String = "Done"
    override val deleteTitle: String = "Delete confirmation"
    override val deleteMessage: (practiceTitle: String) -> String = {
        "Are you sure you want to delete \"$it\" practice?"
    }
    override val deleteButtonDefault: String = "Delete"
    override val deleteButtonCompleted: String = "Done"
    override val unknownTitle: String = "Unknown characters"
    override val unknownMessage: (characters: List<String>) -> String = {
        "Characters ${it.joinToString()} are not found"
    }
    override val unknownButton: String = "Close"
}

object EnglishPracticePreviewStrings : PracticePreviewStrings {
    override val emptyListMessage: String = "Nothing here"
    override val detailsGroupTitle: (index: Int) -> String = { "Group $it" }
    override val reviewStateRecently: String = "Recently reviewed"
    override val reviewStateNeedReview: String = "Review recommended"
    override val reviewStateNever: String = "New"
    override val firstTimeReviewMessage: (LocalDateTime?) -> String = {
        "First time studied: " + when (it) {
            null -> "Never"
            else -> groupDetailsDateTimeFormatter(it)
        }
    }
    override val lastTimeReviewMessage: (LocalDateTime?) -> String = {
        "Last time studied: " + when (it) {
            null -> "Never"
            else -> groupDetailsDateTimeFormatter(it)
        }
    }
    override val groupDetailsButton: String = "Start"
    override val practiceTypeWriting: String = "Writing"
    override val practiceTypeReading: String = "Reading"
    override val filterAll: String = "All"
    override val filterReviewOnly: String = "Review only"
    override val filterNewOnly: String = "New only"
    override val sortOptionAddOrder: String = "Add order"
    override val sortOptionAddOrderHint: String = "↑ New items last\n↓ New items first"
    override val sortOptionFrequency: String = "Frequency"
    override val sortOptionFrequencyHint: String =
        "Occurrence frequency of a character in newspapers\n↑ Frequent first\n↓ Frequent last"
    override val sortOptionName: String = "Name"
    override val sortOptionNameHint: String = "↑ Smaller first\n↓ Smaller last"
    override val screenConfigDialog: PracticePreviewScreenConfigDialogStrings =
        EnglishPracticePreviewScreenConfigDialogStrings
    override val multiselectTitle: (selectedCount: Int) -> String = { "$it Selected" }
    override val multiselectDataNotLoaded: String = "Loading, wait a moment…"
    override val multiselectNoSelected: String = "Select at least one group"
    override val multiselectDialog: MultiselectDialogStrings = EnglishMultiselectDialogStrings
}

object EnglishPracticePreviewScreenConfigDialogStrings : PracticePreviewScreenConfigDialogStrings {
    override val title: String = "Screen Configurations"
    override val practiceType: String = "Practice Type"
    override val filter: String = "Filter"
    override val sorting: String = "Sorting"
    override val buttonCancel: String = "Cancel"
    override val buttonApply: String = "Apply"
}

object EnglishMultiselectDialogStrings : MultiselectDialogStrings {
    override val title: String = "Review options"
    override val message: String =
        "Start review practice with random characters from selected groups"
    override val selected: String = "Selected"
    override val button: String = "Start"
}

object EnglishCommonPracticeStrings : CommonPracticeStrings {
    override val leaveDialogTitle: String = "Leave practice?"
    override val leaveDialogMessage: String = "Progress will be lost"
    override val leaveDialogButton: String = "Confirm"

    override val configurationTitle: String = "Practice Configuration"
    override val collapsablePracticeItemsTitle: (Int) -> String = { "Practice items ($it)" }
    override val shuffleConfigurationTitle: String = "Shuffle"
    override val shuffleConfigurationMessage: String = "Randomizes characters review order"
    override val configurationCompleteButton: String = "Start"

    override val savingTitle: String = "Saving"
    override val savingPreselectTitle: String = "Select characters to revisit tomorrow"
    override val savingPreselectCount: (Int) -> String = {
        "Preselect characters with more than $it mistakes"
    }
    override val savingMistakesMessage: (count: Int) -> String = {
        if (it == 1) "1 mistake" else "$it mistakes"
    }
    override val savingButton: String = "Save"

    override val savedTitle: String = "Summary"
    override val savedReviewedCountLabel: String = "Characters reviewed"
    override val savedTimeSpentLabel: String = "Time spent"
    override val savedAccuracyLabel: String = "Accuracy"
    override val savedRepeatCharactersLabel: String = "Characters to revisit"
    override val savedRetainedCharactersLabel: String = "Retained characters"
    override val savedButton: String = "Finish"
}

object EnglishWritingPracticeStrings : WritingPracticeStrings {
    override val studyNewTitle: String = "Study new characters"
    override val studyNewMessage: String =
        "Adds additional practice step with writing hints for new characters"
    override val noTranslationLayoutTitle: String = "No translation layout"
    override val noTranslationLayoutMessage: String =
        "Hides character translations during writing practice"
    override val leftHandedModeTitle: String = "Left-handed mode"
    override val leftHandedModeMessage: String =
        "Adjusts position of input in landscape mode of writing practice screen"

    override val headerWordsMessage: (count: Int) -> String = {
        "Expressions " + if (it > WritingPracticeScreenContract.WordsLimit) "(100+)" else "($it)"
    }
    override val wordsBottomSheetTitle: String = "Expressions"
    override val studyFinishedButton: String = "Review"
    override val nextButton: String = "Good"
    override val repeatButton: String = "Bad"
}

object EnglishReadingPracticeString : ReadingPracticeStrings {
    override val words: String = "Expressions"
    override val showAnswerButton: String = "Show Answer"
    override val goodButton: String = "Good"
    override val repeatButton: String = "Bad"
}

object EnglishKanjiInfoStrings : KanjiInfoStrings {
    override val strokesMessage: (count: Int) -> AnnotatedString = {
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(it.toString()) }
            if (it == 1) append(" stroke")
            else append(" strokes")
        }
    }
    override val clipboardCopyMessage: String = "Copied"
    override val radicalsSectionTitle: (count: Int) -> String = { "Radicals ($it)" }
    override val noRadicalsMessage: String = "No radicals"
    override val wordsSectionTitle: (count: Int) -> String = { "Expressions ($it)" }
    override val romajiMessage: (romaji: String) -> String = { "Romaji: $it" }
    override val gradeMessage: (grade: Int) -> String = {
        when {
            it <= 6 -> "Jōyō kanji, taught in $it grade"
            it == 8 -> "Jōyō kanji, taught in junior high"
            it >= 9 -> "Jinmeiyō kanji, used in names"
            else -> throw IllegalStateException("Unknown grade $it")
        }
    }
    override val jlptMessage: (level: Int) -> String = { "JLPT level $it" }
    override val frequencyMessage: (frequency: Int) -> String = {
        "$it of 2500 most used kanji in newspapers"
    }
    override val noDataMessage: String = "No data"

}

object EnglishReminderNotificationStrings : ReminderNotificationStrings {
    override val channelName: String = "Reminder Notifications"
    override val title: String = "It's study time!"
    override val message: (Int, Int) -> String = { learn, review ->
        "There are $learn characters to learn and $review to review today"
    }
}
