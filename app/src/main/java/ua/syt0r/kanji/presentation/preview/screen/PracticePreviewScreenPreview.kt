package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui.PracticePreviewScreenUI


@Preview
@Composable
private fun DarkLoadedPreview(
    useDarkTheme: Boolean = true,
    isMultiselectEnabled: Boolean = false
) {
    AppTheme(useDarkTheme = useDarkTheme) {
        val state = remember {
            mutableStateOf(
                ScreenState.Loaded.Groups(
                    title = "Test Practice",
                    configuration = PracticePreviewScreenConfiguration(),
                    allItems = (1..20).map { PracticePreviewItem.random() },
                    groups = (1..20).map { PracticeGroup.random(it, true) },
                    isSelectionModeEnabled = isMultiselectEnabled,
                    kanaGroupsMode = false,
                    selectedItems = emptySet()
                )
            )
        }
        PracticePreviewScreenUI(
            state = state,
            onConfigurationUpdated = {},
            onUpButtonClick = {},
            onEditButtonClick = {},
            onCharacterClick = {},
            onStartPracticeClick = { _ -> },
            onDismissSelectionModeClick = {},
            onEnableMultiselectClick = {},
            onCharacterSelectionToggled = {},
            onGroupClickInMultiselectMode = {},
            onMultiselectPracticeStart = {},
            selectAllClick = {},
            deselectAllClick = {}
        )
    }
}

@Preview(device = Devices.PIXEL_C)
@Composable
private fun LightLoadedPreview() {
    DarkLoadedPreview(useDarkTheme = false, isMultiselectEnabled = true)
}
