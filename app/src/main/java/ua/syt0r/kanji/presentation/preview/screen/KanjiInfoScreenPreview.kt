package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.core.japanese.CharacterClassification
import ua.syt0r.kanji.core.japanese.getHiraganaReading
import ua.syt0r.kanji.presentation.common.PaginatableJapaneseWordList
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.ui.KanjiInfoScreenUI
import kotlin.random.Random


@Preview
@Composable
private fun Preview(
    state: ScreenState = ScreenState.Loading
) {

    AppTheme {
        KanjiInfoScreenUI(
            char = PreviewKanji.kanji,
            state = rememberUpdatedState(state),
            onUpButtonClick = {},
            onCopyButtonClick = {},
            onFuriganaItemClick = {},
            onScrolledToBottom = {}
        )
    }

}

@Preview
@Composable
private fun NoDataPreview() {
    Preview(ScreenState.NoData)
}

@Preview
@Composable
private fun KanaPreview() {
    Preview(
        state = ScreenState.Loaded.Kana(
            character = "ぢ",
            strokes = PreviewKanji.strokes,
            radicals = emptyList(),
            words = rememberUpdatedState(PaginatableJapaneseWordList(200, emptyList())),
            kanaSystem = CharacterClassification.Kana.Hiragana,
            reading = getHiraganaReading('ぢ'),
        )
    )
}

@Preview(locale = "ja")
@Composable
private fun KanjiPreview() {
    Preview(
        state = ScreenState.Loaded.Kanji(
            character = PreviewKanji.kanji,
            strokes = PreviewKanji.strokes,
            radicals = PreviewKanji.radicals,
            meanings = PreviewKanji.meanings,
            on = PreviewKanji.on,
            kun = PreviewKanji.kun,
            grade = 1,
            jlptLevel = 5,
            frequency = 1,
            words = rememberUpdatedState(
                PaginatableJapaneseWordList(200, PreviewKanji.randomWords(30))
            ),
            wanikaniLevel = Random.nextInt(1, 60)
        )
    )
}
