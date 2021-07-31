package ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.ui.WritingPracticePreviewScreenUI

@Composable
fun WritingPracticePreviewScreen(
    navigator: MainContract.Navigation,
    onKanjiClicked: (String) -> Unit = {}
) {

    WritingPracticePreviewScreenUI()

}
