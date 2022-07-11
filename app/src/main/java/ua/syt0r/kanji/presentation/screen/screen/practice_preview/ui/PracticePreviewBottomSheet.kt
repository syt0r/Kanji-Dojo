package ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.CustomDropdownMenu
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.*
import java.time.LocalDateTime
import kotlin.random.Random

val PracticePreviewBottomSheetPeekHeight = 64.dp
private val ExpandableBottomSheetContentItemHeight = 56.dp

@Composable
fun PracticePreviewBottomSheet(
    screenState: PracticePreviewScreenContract.ScreenState,
    isCollapsed: Boolean,
    onToggleButtonClick: () -> Unit = {},
    onPracticeModeSelected: (PracticeMode) -> Unit = {},
    onShuffleSelected: (Boolean) -> Unit = {},
    onSelectionOptionSelected: (SelectionOption) -> Unit = {},
    onSelectionInputChanged: (String) -> Unit = {}
) {

    Logger.logMethod()

    val loadedState = screenState as? PracticePreviewScreenContract.ScreenState.Loaded

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PracticePreviewBottomSheetPeekHeight)
                .clickable(onClick = onToggleButtonClick)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onToggleButtonClick
                ) {
                    val icon = if (isCollapsed) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    }
                    Icon(icon, null)
                }

                val itemsCount = loadedState?.selectedCharacters?.size ?: 0

                Row {
                    Text(
                        text = "$itemsCount",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selected", style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alignByBaseline()
                    )
                }

            }

        }

        Column(
            Modifier
                .fillMaxWidth()
                // Need to set default expandable section height, otherwise bottom sheet
                // automatically expands when state becomes loaded because it's content grows
                .defaultMinSize(minHeight = ExpandableBottomSheetContentItemHeight * 2)
                .verticalScroll(rememberScrollState())
        ) {

            if (loadedState != null) {
                ExpandableContent(
                    configuration = loadedState.selectionConfiguration,
                    onPracticeModeSelected = onPracticeModeSelected,
                    onShuffleSelected = onShuffleSelected,
                    onSelectionOptionSelected = onSelectionOptionSelected,
                    onSelectionInputChanged = onSelectionInputChanged
                )
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableContent(
    configuration: SelectionConfiguration,
    onPracticeModeSelected: (PracticeMode) -> Unit = {},
    onShuffleSelected: (Boolean) -> Unit = {},
    onSelectionOptionSelected: (SelectionOption) -> Unit = {},
    onSelectionInputChanged: (String) -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ExpandableBottomSheetContentItemHeight)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var isDropdownExpanded by remember { mutableStateOf(false) }
        Text(text = "Practice Mode", modifier = Modifier.weight(1f))
        Box {
            TextButton(
                modifier = Modifier.animateContentSize(),
                onClick = { isDropdownExpanded = true }
            ) {
                Text(text = configuration.practiceMode.title)
                Icon(Icons.Default.ArrowDropDown, null)
            }
            CustomDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                AvailablePracticeModes.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.title) },
                        onClick = {
                            onPracticeModeSelected(it)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ExpandableBottomSheetContentItemHeight)
            .clickable { onShuffleSelected(!configuration.shuffle) }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Shuffle", modifier = Modifier.weight(1f))
        Switch(
            checked = configuration.shuffle,
            onCheckedChange = { onShuffleSelected(!configuration.shuffle) },
            colors = SwitchDefaults.colors(
                uncheckedTrackColor = MaterialTheme.colorScheme.background
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ExpandableBottomSheetContentItemHeight)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Select characters:")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ExpandableBottomSheetContentItemHeight)
            .clickable { onSelectionOptionSelected(SelectionOption.FirstItems) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = configuration.option == SelectionOption.FirstItems,
            onClick = { onSelectionOptionSelected(SelectionOption.FirstItems) }
        )
        Text("First")
        Spacer(modifier = Modifier.width(20.dp))
        TextField(
            value = configuration.firstItemsText,
            onValueChange = { onSelectionInputChanged(it) },
            modifier = Modifier.width(IntrinsicSize.Min),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = configuration.option == SelectionOption.FirstItems
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ExpandableBottomSheetContentItemHeight)
            .clickable { onSelectionOptionSelected(SelectionOption.All) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = configuration.option == SelectionOption.All,
            onClick = { onSelectionOptionSelected(SelectionOption.All) }
        )
        Text("All", Modifier.weight(1f))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ExpandableBottomSheetContentItemHeight)
            .clickable { onSelectionOptionSelected(SelectionOption.ManualSelection) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = configuration.option == SelectionOption.ManualSelection,
            onClick = { onSelectionOptionSelected(SelectionOption.ManualSelection) }
        )
        Text("Manually", Modifier.weight(1f))
    }
}


@Preview(showBackground = true, group = "bottom sheet")
@Composable
private fun BottomSheetPreview() {
    AppTheme {
        PracticePreviewBottomSheet(
            screenState = PracticePreviewScreenContract.ScreenState.Loaded(
                practiceId = Random.nextLong(),
                characterData = (0..40).map {
                    PreviewCharacterData(
                        character = Random.nextInt().toChar().toString(),
                        frequency = 0,
                        lastReviewTime = LocalDateTime.MIN
                    )
                },
                selectionConfiguration = SelectionConfiguration.default,
                sortConfiguration = SortConfiguration.default,
                selectedCharacters = sortedSetOf()
            ),
            isCollapsed = false
        )
    }
}

@Preview(showBackground = true, group = "bottom sheet")
@Composable
private fun DarkBottomSheetPreview() {
    AppTheme(useDarkTheme = true) {
        Surface {
            PracticePreviewBottomSheet(
                screenState = PracticePreviewScreenContract.ScreenState.Loaded(
                    practiceId = Random.nextLong(),
                    characterData = (0..40).map {
                        PreviewCharacterData(
                            character = Random.nextInt().toChar().toString(),
                            frequency = 0,
                            lastReviewTime = LocalDateTime.MIN
                        )
                    },
                    selectionConfiguration = SelectionConfiguration.default.copy(shuffle = false),
                    sortConfiguration = SortConfiguration.default,
                    selectedCharacters = sortedSetOf()
                ),
                isCollapsed = false
            )
        }
    }
}