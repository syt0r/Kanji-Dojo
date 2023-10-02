package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData
import kotlin.time.Duration.Companion.minutes

@Preview
@Composable
private fun UiPreview(
    state: ScreenState = ScreenState.Loading
) {
    AppTheme(useDarkTheme = false) {
        ReadingPracticeScreenUI(
            state = rememberUpdatedState(state),
            navigateBack = {},
            onConfigured = {},
            onOptionSelected = {},
            onPracticeSaveClick = {},
            onFinishButtonClick = {}
        )
    }
}

@Preview
@Composable
private fun KanaPreview() {
    UiPreview(
        state = ScreenState.Review(
            progress = ReadingPracticeContract.ReviewProgress(6, 0, 0),
            characterData = ReadingReviewCharacterData.Kana(
                reading = "A",
                classification = CharactersClassification.Kana.Hiragana,
                character = "あ",
                words = PreviewKanji.randomWords()
            )
        )
    )
}

@Preview
@Composable
private fun KanjiPreview() {
    UiPreview(
        state = ScreenState.Review(
            progress = ReadingPracticeContract.ReviewProgress(6, 0, 0),
            characterData = ReadingReviewCharacterData.Kanji(
                character = PreviewKanji.kanji,
                on = PreviewKanji.on,
                kun = PreviewKanji.kun,
                meanings = PreviewKanji.meanings,
                words = PreviewKanji.randomWords()
            )
        )
    )
}

@Preview
@Composable
private fun SummaryPreview() {
    UiPreview(
        state = ScreenState.Saved(
            practiceDuration = 1.minutes,
            accuracy = 1f,
            repeatCharacters = (1..3).map { PreviewKanji.randomKanji() },
            goodCharacters = (1..6).map { PreviewKanji.randomKanji() }
        )
    )
}

@Preview(device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    KanaPreview()
}