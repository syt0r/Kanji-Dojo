package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import ua.syt0r.kanji.core.japanese.CharacterClassification
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeCharacterReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeProgress
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewCharacterDetails
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingScreenLayoutConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui.WritingPracticeScreenUI
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Preview
@Composable
private fun KanjiPreview(
    darkTheme: Boolean = false,
    isStudyMode: Boolean = false
) {
    AppTheme(darkTheme) {
        WritingPracticeScreenUI(
            state = WritingPracticeScreenUIPreviewUtils.reviewState(
                isKana = false,
                isStudyMode = isStudyMode,
                wordsCount = 10
            ),
            navigateBack = {},
            onConfigured = {},
            submitUserInput = { TODO() },
            onHintClick = {},
            onPracticeSaveClick = {},
            onPracticeCompleteButtonClick = {},
            onNextClick = {},
            toggleRadicalsHighlight = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 600, locale = "ja")
@Composable
private fun KanjiStudyPreview() {
    KanjiPreview(darkTheme = true, isStudyMode = true)
}

@Preview(showBackground = true)
@Composable
private fun KanaPreview(
    darkTheme: Boolean = false,
    isStudyMode: Boolean = false
) {
    AppTheme(darkTheme) {
        WritingPracticeScreenUI(
            state = WritingPracticeScreenUIPreviewUtils.reviewState(
                isKana = true,
                isStudyMode = isStudyMode
            ),
            navigateBack = {},
            onConfigured = {},
            submitUserInput = { TODO() },
            onHintClick = {},
            onPracticeSaveClick = {},
            onPracticeCompleteButtonClick = {},
            onNextClick = {},
            toggleRadicalsHighlight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KanaStudyPreview() {
    KanaPreview(darkTheme = true, isStudyMode = true)
}

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {
    AppTheme {
        WritingPracticeScreenUI(
            state = ScreenState.Loading.run { mutableStateOf(this) },
            navigateBack = {},
            onConfigured = {},
            submitUserInput = { TODO() },
            onHintClick = {},
            onPracticeSaveClick = { },
            onPracticeCompleteButtonClick = {},
            onNextClick = {},
            toggleRadicalsHighlight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavingPreview() {
    AppTheme {
        WritingPracticeScreenUI(
            state = ScreenState.Saving(
                reviewResultList = (0..20).map {
                    PracticeCharacterReviewResult(
                        character = PreviewKanji.randomKanji(),
                        mistakes = Random.nextInt(0, 9)
                    )
                },
                outcomeSelectionConfiguration = OutcomeSelectionConfiguration(2),
            ).run { mutableStateOf(this) },
            navigateBack = {},
            onConfigured = {},
            submitUserInput = { TODO() },
            onHintClick = {},
            onPracticeSaveClick = { },
            onPracticeCompleteButtonClick = {},
            onNextClick = {},
            toggleRadicalsHighlight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedPreview() {
    AppTheme {
        WritingPracticeScreenUI(
            state = ScreenState.Saved(
                practiceDuration = 63.seconds.plus(5.milliseconds),
                accuracy = 88.6666f,
                repeatCharacters = listOf("国", "年"),
                goodCharacters = "時行見月後前生五間上東四今".map { it.toString() }
            ).run { mutableStateOf(this) },
            navigateBack = {},
            onConfigured = {},
            submitUserInput = { TODO() },
            onHintClick = {},
            onPracticeSaveClick = { },
            onPracticeCompleteButtonClick = {},
            onNextClick = {},
            toggleRadicalsHighlight = {}
        )
    }
}

@Preview(device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    KanjiPreview(darkTheme = true)
}

object WritingPracticeScreenUIPreviewUtils {

    fun reviewState(
        isKana: Boolean = true,
        isStudyMode: Boolean = false,
        wordsCount: Int = 3,
        progress: PracticeProgress = PracticeProgress(2, 2, 2, 0),
        drawnStrokesCount: Int = 2
    ): State<ScreenState.Review> {
        val words = PreviewKanji.randomWords(wordsCount)
        return ScreenState.Review(
            shouldHighlightRadicals = mutableStateOf(false),
            layoutConfiguration = WritingScreenLayoutConfiguration(
                noTranslationsLayout = false,
                radicalsHighlight = mutableStateOf(true),
                leftHandedMode = false,
                showStrokeCount = true
            ),
            reviewState = MutableStateFlow(
                WritingReviewData(
                    progress = progress,
                    characterData = when {
                        isKana -> WritingReviewCharacterDetails.KanaReviewDetails(
                            character = "あ",
                            strokes = PreviewKanji.strokes,
                            radicals = PreviewKanji.radicals,
                            words = words,
                            encodedWords = words,
                            kanaSystem = CharacterClassification.Kana.Hiragana,
                            romaji = "A"
                        )

                        else -> WritingReviewCharacterDetails.KanjiReviewDetails(
                            character = PreviewKanji.kanji,
                            strokes = PreviewKanji.strokes,
                            radicals = PreviewKanji.radicals,
                            words = words,
                            encodedWords = words,
                            kun = PreviewKanji.kun,
                            on = PreviewKanji.on,
                            meanings = PreviewKanji.meanings
                        )
                    },
                    isStudyMode = isStudyMode,
                    drawnStrokesCount = mutableStateOf(drawnStrokesCount),
                    currentStrokeMistakes = mutableStateOf(0),
                    currentCharacterMistakes = mutableStateOf(0)
                )
            )
        ).run { mutableStateOf(this) }
    }

}