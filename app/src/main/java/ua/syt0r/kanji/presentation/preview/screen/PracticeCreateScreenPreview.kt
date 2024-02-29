package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.ProcessingStatus
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui.PracticeCreateScreenUI


@Composable
private fun BasePreview(
    configuration: MainDestination.CreatePractice,
    state: ScreenState
) {
    AppTheme {
        PracticeCreateScreenUI(
            configuration = configuration,
            state = rememberUpdatedState(state),
            navigateBack = {},
            onPracticeDeleteClick = {},
            onDeleteAnimationCompleted = {},
            onCharacterInfoClick = {},
            onCharacterDeleteClick = {},
            onCharacterRemovalCancel = {},
            onSaveConfirmed = {},
            onSaveAnimationCompleted = {},
            submitKanjiInput = { TODO() },
        )
    }
}

@Preview
@Composable
private fun CreatePreview() {
    BasePreview(
        configuration = MainDestination.CreatePractice.New,
        state = ScreenState.Loaded(
            initialPracticeTitle = null,
            characters = (2..80)
                .map { PreviewKanji.randomKanji() },
            charactersToRemove = emptyList(),
            wasEdited = false,
            processingStatus = ProcessingStatus.Loaded
        )
    )
}

@Preview
@Composable
private fun EditPreview() {
    BasePreview(
        configuration = MainDestination.CreatePractice.EditExisting(1),
        state = ScreenState.Loaded(
            initialPracticeTitle = null,
            characters = (2..80)
                .map { PreviewKanji.randomKanji() },
            charactersToRemove = emptyList(),
            wasEdited = false,
            processingStatus = ProcessingStatus.Loaded
        )
    )
}
