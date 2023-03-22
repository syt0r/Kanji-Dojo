package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui.WritingPracticeScreenUI
import kotlin.random.Random

@Preview
@Composable
private fun KanjiPreview(
    darkTheme: Boolean = false,
    isStudyMode: Boolean = true
) {
    AppTheme(darkTheme) {
        WritingPracticeScreenUI(
            state = WritingPracticeScreenUIPreviewUtils.reviewState(
                isKana = false,
                isStudyMode = isStudyMode,
                wordsCount = 10
            ),
            navigateBack = {},
            submitUserInput = { TODO() },
            onHintClick = {},
            onReviewItemClick = {},
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
            submitUserInput = { TODO() },
            onHintClick = {},
            onReviewItemClick = {},
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
            submitUserInput = { TODO() },
            onHintClick = {},
            onReviewItemClick = {},
            onPracticeCompleteButtonClick = {},
            onNextClick = {},
            toggleRadicalsHighlight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryPreview() {
    AppTheme {
        WritingPracticeScreenUI(
            state = ScreenState.Summary.Saved(
                reviewResultList = (0..20).map {
                    ReviewResult(
                        characterReviewResult = CharacterReviewResult(
                            character = PreviewKanji.kanji,
                            practiceId = 0,
                            mistakes = Random.nextInt(0, 9)
                        ),
                        reviewScore = ReviewScore.values().random()
                    )
                },
                eligibleForInAppReview = false
            ).run { mutableStateOf(this) },
            navigateBack = {},
            submitUserInput = { TODO() },
            onHintClick = {},
            onReviewItemClick = {},
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
        progress: WritingPracticeProgress = WritingPracticeProgress(2, 2, 2, 0),
        drawnStrokesCount: Int = 2
    ): State<ScreenState.Review> {
        val words = PreviewKanji.randomWords(wordsCount)
        return ScreenState.Review(
            configuration = WritingScreenConfiguration(
                shouldHighlightRadicals = false,
                noTranslationsLayout = false,
                leftHandedMode = false
            ),
            reviewState = mutableStateOf(
                WritingReviewData(
                    progress = progress,
                    characterData = when {
                        isKana -> ReviewCharacterData.KanaReviewData(
                            character = "あ",
                            strokes = PreviewKanji.strokes,
                            radicals = PreviewKanji.radicals,
                            words = words,
                            encodedWords = words,
                            kanaSystem = CharactersClassification.Kana.Hiragana,
                            romaji = "A"
                        )
                        else -> ReviewCharacterData.KanjiReviewData(
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
                    drawnStrokesCount = drawnStrokesCount
                )
            )
        ).run { mutableStateOf(this) }
    }

}