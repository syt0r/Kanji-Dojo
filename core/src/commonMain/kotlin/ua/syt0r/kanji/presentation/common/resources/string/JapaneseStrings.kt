package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.presentation.common.withClickableUrl
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import kotlin.time.Duration

object JapaneseStrings : Strings {

    override val appName: String = "Kanji Dojo"

    override val hiragana: String = "ひらがな"
    override val katakana: String = "カタカナ"

    override val kunyomi: String = "訓読み"
    override val onyomi: String = "音読み"

    override val loading: String = "読み込み中"

    override val home: HomeStrings = JapaneseHomeStrings
    override val practiceDashboard = JapanesePracticeDashboardStrings
    override val createPracticeDialog = JapaneseCreatePracticeDialogStrings
    override val dailyGoalDialog = JapaneseDailyGoalDialogStrings
    override val stats: StatsStrings = JapaneseStatsStrings
    override val search: SearchStrings = JapaneseSearchStrings
    override val alternativeDialog: AlternativeDialogStrings = JapaneseAlternativeDialogStrings
    override val settings: SettingsStrings = JapaneseSettingsStrings
    override val reminderDialog: ReminderDialogStrings = JapaneseReminderDialogStrings
    override val about: AboutStrings = JapaneseAboutStrings
    override val practiceImport: PracticeImportStrings = JapanesePracticeImportStrings
    override val practiceCreate: PracticeCreateStrings = JapanesePracticeCreateStrings
    override val practicePreview: PracticePreviewStrings = JapanesePracticePreviewStrings
    override val commonPractice: CommonPracticeStrings = JapaneseCommonPracticeStrings
    override val writingPractice: WritingPracticeStrings = JapaneseWritingPracticeStrings
    override val readingPractice: ReadingPracticeStrings = JapaneseReadingPracticeString
    override val kanjiInfo: KanjiInfoStrings = JapaneseKanjiInfoStrings

    override val urlPickerMessage: String = "開く"
    override val urlPickerErrorMessage: String = "ブラウザーが見つかりません"

    override val reminderNotification: ReminderNotificationStrings =
        JapaneseReminderNotificationStrings

}

object JapaneseHomeStrings : HomeStrings {
    override val screenTitle: String = JapaneseStrings.appName
    override val dashboardTabLabel: String = "練習"
    override val statsTabLabel: String = "統計"
    override val searchTabLabel: String = "検索"
    override val settingsTabLabel: String = "設定"
}

object JapanesePracticeDashboardStrings : PracticeDashboardStrings {
    override val emptyScreenMessage = { color: Color ->
        buildAnnotatedString {
            append("アプリを使うには練習セットが必要です。\n")
            withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) { append("+") }
            append("ボタンを押して、練習セットを作成してください。")
        }
    }

    override val mergeButton: String = "統合"
    override val mergeCancelButton: String = "キャンセル"
    override val mergeAcceptButton: String = "統合"
    override val mergeTitle: String = "複数のセットを1つに統合"
    override val mergeTitleHint: String = "タイトルを入力"
    override val mergeSelectedCount: (Int) -> String = { "$it 個選択中" }
    override val mergeClearSelectionButton: String = "クリア"

    override val mergeDialogTitle: String = "統合の確認"
    override val mergeDialogMessage: (String, List<String>) -> String = { newTitle, mergedTitles ->
        "以下の${mergedTitles.size}つのセットが新しいセット「$newTitle」に統合されます: ${mergedTitles.joinToString()}"
    }
    override val mergeDialogCancelButton: String = "キャンセル"
    override val mergeDialogAcceptButton: String = "統合"

    override val sortButton: String = "並べ替え"
    override val sortCancelButton: String = "キャンセル"
    override val sortAcceptButton: String = "適用"
    override val sortTitle: String = "セットの順序を変更"
    override val sortByTimeTitle: String = "最終練習時間で並べ替える"

    override val itemTimeMessage: (Duration?) -> String = {
        "最終練習日: " + when {
            it == null -> "なし"
            it.inWholeDays > 0 -> "${it.inWholeDays}日前"
            else -> "1日以内"
        }
    }

    override val itemWritingTitle: String = "書き"
    override val itemReadingTitle: String = "読み"
    override val itemTotal: String = "合計"
    override val itemDone: String = "完了"
    override val itemReview: String = "復習"
    override val itemNew: String = "未習"
    override val itemQuickPracticeTitle: String = "クイック練習"
    override val itemQuickPracticeLearn: (Int) -> String = { "新しく学習 ($it)" }
    override val itemQuickPracticeReview: (Int) -> String = { "復習 ($it)" }
    override val itemGraphProgressTitle: String = "完了率"

    override val dailyIndicatorPrefix: String = "毎日の目標: "
    override val dailyIndicatorCompleted: String = "完了"
    override val dailyIndicatorDisabled: String = "無効"
    override val dailyIndicatorNew: (Int) -> String = { "$it 学習" }
    override val dailyIndicatorReview: (Int) -> String = { "$it 復習" }
}

object JapaneseCreatePracticeDialogStrings : CreatePracticeDialogStrings {
    override val title: String = "練習セットの作成"
    override val selectMessage: String = "選ぶ（仮名、教育漢字など）"
    override val createMessage: String = "空のセットから作る"
}

object JapaneseDailyGoalDialogStrings : DailyGoalDialogStrings {
    override val title: String = "毎日の目標"
    override val message: String = "クイック練習と通知の有無に影響します"
    override val enabledLabel: String = "有効"
    override val studyLabel: String = "新しい文字"
    override val reviewLabel: String = "復習"
    override val noteMessage: String = "注意: 文字の書きと読みは別に数えます"
    override val applyButton: String = "適用"
    override val cancelButton: String = "キャンセル"
}

private fun formatDuration(duration: Duration): String = when {
    duration.inWholeHours > 0 -> "${duration.inWholeHours}時 ${duration.inWholeMinutes % 60}分"
    duration.inWholeMinutes > 0 -> "${duration.inWholeMinutes}分 ${duration.inWholeSeconds % 60}秒"
    else -> "${duration.inWholeSeconds}秒"
}

object JapaneseStatsStrings : StatsStrings {
    override val todayTitle: String = "今日"
    override val monthTitle: String = "今月"
    override val monthLabel: (day: LocalDate) -> String =
        { "${it.year}年${it.monthNumber}月" }
    override val yearTitle: String = "今年"
    override val yearDaysPracticedLabel = { practicedDays: Int, daysInYear: Int ->
        "練習日数: $practicedDays/$daysInYear"
    }
    override val totalTitle: String = "合計"
    override val timeSpentTitle: String = "掛けた時間"
    override val reviewsCountTitle: String = "練習回数"
    override val formattedDuration: (Duration) -> String = { formatDuration(it) }
    override val charactersStudiedTitle: String = "学習済みの文字"
}


object JapaneseSearchStrings : SearchStrings {
    override val inputHint: String = "文字または単語を入力"
    override val charactersTitle: (count: Int) -> String = { "文字 ($it)" }
    override val wordsTitle: (count: Int) -> String = { "単語 ($it)" }
    override val radicalsSheetTitle: String = "部首で検索"
    override val radicalsFoundCharacters: String = "見つかった文字"
    override val radicalsEmptyFoundCharacters: String = "何も見つかりませんでした"
    override val radicalSheetRadicalsSectionTitle: String = "部首"
}

object JapaneseAlternativeDialogStrings : AlternativeDialogStrings {
    override val title: String = "別の単語"
    override val readingsTitle: String = "読み方"
    override val meaningsTitle: String = "意味"
    override val button: String = "閉じる"
}

object JapaneseSettingsStrings : SettingsStrings {
    override val analyticsTitle: String = "分析レポート"
    override val analyticsMessage: String = "アプリを向上させるために匿名データの送信を許可する"
    override val themeTitle: String = "テーマ"
    override val themeSystem: String = "システムに従う"
    override val themeLight: String = "ライト"
    override val themeDark: String = "ダーク"
    override val reminderTitle: String = "リマインダー通知"
    override val reminderEnabled: String = "有効"
    override val reminderDisabled: String = "無効"
    override val aboutTitle: String = "このアプリについて"
}

object JapaneseReminderDialogStrings : ReminderDialogStrings {
    override val title: String = "リマインダー通知"
    override val noPermissionLabel: String = "通知の権限がありません"
    override val noPermissionButton: String = "許可する"
    override val enabledLabel: String = "通知"
    override val timeLabel: String = "時間"
    override val cancelButton: String = "キャンセル"
    override val applyButton: String = "適用"
}

object JapaneseAboutStrings : AboutStrings by EnglishAboutStrings {
    override val title: String = "このアプリについて"
    override val version: (versionName: String) -> String = { "バージョン: $it" }
}

object JapanesePracticeImportStrings : PracticeImportStrings {

    override val title: String = "選択"

    override val kanaTitle: String = "仮名"

    override val kanaDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("仮名は、最も簡単な日本語の文字です。仮名は二種類に分かれます。\n")
            append("・平仮名（ひらがな）─ 日本語の単語や音を伝えるときに使う\n")
            append("・片仮名（かたかな）─ 主に外来語の単語を書くときに使う")
        }
    }
    override val hiragana: String = JapaneseStrings.hiragana
    override val katakana: String = JapaneseStrings.katakana

    override val jltpTitle: String = "日本語能力試験"
    override val jlptDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("日本語能力試験 (JLPT) は、日本語を母語としない人のための日本語の試験です。N5からN1までの難しさがあります。")
        }
    }
    override val jlptItem: (level: Int) -> String = { "JLPT N$it" }

    override val gradeTitle: String = "常用漢字"
    override val gradeDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("常用漢字は、2,136字から成る、よく使われる漢字の表です。内容は以下の通りです。\n")
            append("・最初の1,026字は小学1年生から6年生までが学ぶ。\n")
            append("・以降の1,110字は中学生が学ぶ。")
        }
    }
    override val gradeItemNumbered: (Int) -> String = { "小学${it}年生" }
    override val gradeItemSecondary: String = "中学生"
    override val gradeItemNames: String = "人名用漢字(一)"
    override val gradeItemNamesVariants: String = "人名用漢字(二)（異体字）"

    override val wanikaniTitle: String = EnglishPracticeImportStrings.wanikaniTitle
    override val wanikaniDescription = { urlColor: Color ->
        buildAnnotatedString {
            append("WaniKaniにおけるレベル別の漢字の一覧です。")
            withClickableUrl("https://www.wanikani.com/kanji?difficulty=pleasant", urlColor) {
                append("もっと見る")
            }
        }
    }
    override val wanikaniItem: (Int) -> String = { "WaniKani レベル$it" }

}

object JapanesePracticeCreateStrings : PracticeCreateStrings {
    override val newTitle: String = "作成"
    override val ediTitle: String = "編集"
    override val searchHint: String = "文字を入力"
    override val infoAction: String = "情報"
    override val returnAction: String = "戻る"
    override val removeAction: String = "消去"
    override val saveTitle: String = "変更の保存"
    override val saveInputHint: String = "名前"
    override val saveButtonDefault: String = "保存"
    override val saveButtonCompleted: String = "完了"
    override val deleteTitle: String = "削除の確認"
    override val deleteMessage: (practiceTitle: String) -> String = {
        "練習セット「$it」を削除してもよろしいですか？"
    }
    override val deleteButtonDefault: String = "削除"
    override val deleteButtonCompleted: String = "完了"
    override val unknownTitle: String = "不明な文字"
    override val unknownMessage: (characters: List<String>) -> String = {
        "${it.joinToString()} のデータが見つかりませんでした"
    }
    override val unknownButton: String = "OK"
}

object JapanesePracticePreviewStrings : PracticePreviewStrings {
    override val emptyListMessage: String = "何もありません"
    override val detailsGroupTitle: (index: Int) -> String = { "グループ $it" }
    override val reviewStateRecently: String = "最近に復習済み"
    override val reviewStateNeedReview: String = "復習が必要"
    override val reviewStateNever: String = "新しい"
    override val firstTimeReviewMessage: (LocalDateTime?) -> String = {
        "初めて練習した時間: " + when (it) {
            null -> "なし"
            else -> groupDetailsDateTimeFormatter(it)
        }
    }
    override val lastTimeReviewMessage: (LocalDateTime?) -> String = {
        "最後に練習した時間: " + when (it) {
            null -> "なし"
            else -> groupDetailsDateTimeFormatter(it)
        }
    }
    override val groupDetailsButton: String = "練習を開始"
    override val expectedReviewDate: (LocalDateTime?) -> String = {
        "次回の復習日: ${it?.date ?: "-"}"
    }
    override val lastReviewDate: (LocalDateTime?) -> String = {
        "最後の復習日: ${it?.date ?: "-"}"
    }
    override val repetitions: (Int) -> String = { "連続正解回数: $it" }
    override val lapses: (Int) -> String = { "忘却回数: $it" }

    override val dialogCommon: PracticePreviewDialogCommonStrings =
        JapanesePracticePreviewDialogCommonStrings
    override val practiceTypeDialog: PracticeTypeDialogStrings = JapanesePracticeTypeDialogStrings
    override val filterDialog: FilterDialogStrings = JapaneseFilterDialogStrings
    override val sortDialog: SortDialogStrings = JapaneseSortDialogStrings
    override val layoutDialog: PracticePreviewLayoutDialogStrings =
        JapanesePracticePreviewLayoutDialogStrings

    override val multiselectTitle: (selectedCount: Int) -> String = { "$it 個選択中" }
    override val multiselectDataNotLoaded: String = "しばらくお待ちください…"
    override val multiselectNoSelected: String = "少なくとも1つ選んでください"
    override val kanaGroupsModeActivatedLabel: String = "仮名グループモード"
}

object JapanesePracticePreviewDialogCommonStrings : PracticePreviewDialogCommonStrings {
    override val buttonCancel: String = "キャンセル"
    override val buttonApply: String = "適用"
}

object JapanesePracticeTypeDialogStrings : PracticeTypeDialogStrings {
    override val title: String = "練習の種類"
    override val practiceTypeWriting: String = "書き"
    override val practiceTypeReading: String = "読み"
}

object JapaneseFilterDialogStrings : FilterDialogStrings {
    override val title: String = "表示する文字"
    override val filterAll: String = "すべて"
    override val filterReviewOnly: String = "復習のみ"
    override val filterNewOnly: String = "新規のみ"
}

object JapaneseSortDialogStrings : SortDialogStrings {
    override val title: String = "順序"
    override val sortOptionAddOrder: String = "追加順"
    override val sortOptionAddOrderHint: String = "↑ 新しい文字が最後\n↓ 新しい文字が最初"
    override val sortOptionFrequency: String = "頻出順"
    override val sortOptionFrequencyHint: String =
        "新聞で使われる頻度\n↑ 頻度が高い文字が最初\n↓ 頻度が高い文字が最後"
    override val sortOptionName: String = "符号順"
    override val sortOptionNameHint: String = "↑ 小さい文字が最初\n↓ 小さい文字が最後"
}

object JapanesePracticePreviewLayoutDialogStrings : PracticePreviewLayoutDialogStrings {
    override val title: String = "レイアウト"
    override val singleCharacterOptionLabel: String = "リスト"
    override val groupsOptionLabel: String = "グループ"
    override val kanaGroupsTitle: String = "仮名グループ"
    override val kanaGroupsSubtitle: String =
        "すべての仮名が含まれている場合、五十音に従ってグループのサイズを設定します"
}

object JapaneseCommonPracticeStrings : CommonPracticeStrings {
    override val leaveDialogTitle: String = "練習をやめますか？"
    override val leaveDialogMessage: String = "現在の進行状況は失われます"
    override val leaveDialogButton: String = "やめる"

    override val configurationTitle: String = "練習の設定"
    override val configurationCharactersCount: (Int, Int) -> String = { selected, total ->
        "練習の数 ($selected/$total)"
    }
    override val configurationCharactersPreview: String = "文字のプレビュー"
    override val shuffleConfigurationTitle: String = "順序のシャッフル"
    override val shuffleConfigurationMessage: String = "文字の復習順をランダムにする"
    override val configurationCompleteButton: String = "開始"

    override val savingTitle: String = "練習の保存"
    override val savingPreselectTitle: String = "明日に復習する文字を選択"
    override val savingPreselectCount: (Int) -> String = {
        "${it}回以上のミスがある文字は自動で選択されます"
    }
    override val savingMistakesMessage: (count: Int) -> String = { "${it}回ミス" }
    override val savingButton: String = "保存"

    override val savedTitle: String = "まとめ"
    override val savedReviewedCountLabel: String = "練習した文字の数"
    override val savedTimeSpentLabel: String = "時間"
    override val savedTimeSpentValue: (Duration) -> String = { formatDuration(it) }
    override val savedAccuracyLabel: String = "正解率"
    override val savedRepeatCharactersLabel: String = "復習する文字"
    override val savedRetainedCharactersLabel: String = "覚えた文字"
    override val savedButton: String = "終了"
}

object JapaneseWritingPracticeStrings : WritingPracticeStrings {
    override val hintStrokesTitle: String = "字画のヒント"
    override val hintStrokesMessage: String = "ヒントの表示を設定する"
    override val hintStrokeNewOnlyMode: String = "新しい文字のみ表示"
    override val hintStrokeAllMode: String = "常に表示"
    override val hintStrokeNoneMode: String = "非表示"
    override val noTranslationLayoutTitle: String = "翻訳の非表示"
    override val noTranslationLayoutMessage: String = "書く練習で字義の翻訳を隠す"
    override val leftHandedModeTitle: String = "左手モード"
    override val leftHandedModeMessage: String = "書く練習で横画面の場合、書く場所を左に移す"

    override val headerWordsMessage: (count: Int) -> String = {
        "単語  " + if (it > WritingPracticeScreenContract.WordsLimit) "(100+)" else "($it)"
    }
    override val wordsBottomSheetTitle: String = "語句"
    override val studyFinishedButton: String = "復習"
    override val nextButton: String = "正解"
    override val repeatButton: String = "もう一度"
    override val altStrokeEvaluatorTitle: String = "代替字画認識"
    override val altStrokeEvaluatorMessage: String = "オリジナルの字画認識の代わりに代替のアルゴリズムを使う"
}

object JapaneseReadingPracticeString : ReadingPracticeStrings {
    override val words: String = "単語"
    override val showAnswerButton: String = "正答を表示"
    override val goodButton: String = "正解"
    override val repeatButton: String = "もう一度"
}

object JapaneseKanjiInfoStrings : KanjiInfoStrings {
    override val strokesMessage: (count: Int) -> AnnotatedString = {
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("${it}画") }
        }
    }
    override val clipboardCopyMessage: String = "コピーしました"
    override val radicalsSectionTitle: (count: Int) -> String = { "部首 ($it)" }
    override val noRadicalsMessage: String = "部首なし"
    override val wordsSectionTitle: (count: Int) -> String = { "単語 ($it)" }
    override val romajiMessage: (romaji: String) -> String = { "ローマ字: $it" }
    override val gradeMessage: (grade: Int) -> String = {
        when {
            it <= 6 -> "常用漢字，小学校${it}年で学習"
            it == 8 -> "常用漢字，中学校で学習"
            it >= 9 -> "人名用漢字"
            else -> throw IllegalStateException("Unknown grade $it")
        }
    }
    override val jlptMessage: (level: Int) -> String = { "JLPT レベル$it" }
    override val frequencyMessage: (frequency: Int) -> String = {
        "新聞頻出漢字の2500中${it}番目"
    }
    override val noDataMessage: String = "データなし"

}

object JapaneseReminderNotificationStrings : ReminderNotificationStrings {
    override val channelName: String = "リマインダー通知"
    override val title: String = "勉強の時間です！"
    override val noDetailsMessage: String = "日本語の学習を続ける"
    override val learnOnlyMessage: (Int) -> String = {
        "今日は学習する文字が${it}字残っています"
    }
    override val reviewOnlyMessage: (Int) -> String = {
        "今日は復習する文字が${it}字残っています"
    }
    override val message: (Int, Int) -> String = { learn, review ->
        "今日は学習する文字が${learn}字、復習する文字が${review}字残っています"
    }
}
