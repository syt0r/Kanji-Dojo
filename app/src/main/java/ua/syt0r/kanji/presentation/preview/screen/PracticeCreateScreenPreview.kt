package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui.PracticeCreateScreenUI
import kotlin.random.Random

@Preview
@Composable
private fun CreatePreview() {
    AppTheme {
        PracticeCreateScreenUI(
            configuration = MainDestination.CreatePractice.New,
            state = ScreenState.Loaded(
                initialPracticeTitle = null,
                characters = (2..10)
                    .map {
                        Char(
                            Random.nextInt(Char.MIN_VALUE.code, Char.MAX_VALUE.code)
                        ).toString()
                    }
                    .toSet(),
                charactersPendingForRemoval = emptySet(),
                currentDataAction = DataAction.Loaded
            ).let { rememberUpdatedState(it) }
        )
    }
}

@Preview
@Composable
private fun EditPreview() {
    AppTheme {
        PracticeCreateScreenUI(
            configuration = MainDestination.CreatePractice.EditExisting(practiceId = 1),
            state = ScreenState.Loaded(
                initialPracticeTitle = null,
                characters = (2..10)
                    .map {
                        Char(
                            Random.nextInt(Char.MIN_VALUE.code, Char.MAX_VALUE.code)
                        ).toString()
                    }
                    .toSet(),
                charactersPendingForRemoval = emptySet(),
                currentDataAction = DataAction.Loaded
            ).let { rememberUpdatedState(it) }
        )
    }
}
