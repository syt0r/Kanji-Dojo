package ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.*
import kotlin.random.Random

private val bottomSheetPeekHeight = 56.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PracticePreviewScreenUI(
    title: String,
    screenState: ScreenState,
    onUpButtonClick: () -> Unit = {},
    onEditButtonClick: () -> Unit = {},
    onSortSelected: () -> Unit = {},
    onPracticeModeSelected: (PracticeMode) -> Unit = {},
    onShuffleSelected: (Boolean) -> Unit = {},
    onSelectionOptionSelected: (SelectionOption) -> Unit = {},
    onSelectionCountInputChanged: (String) -> Unit = {},
    startPractice: () -> Unit = {},
    onCharacterClick: (PreviewCharacterData) -> Unit = {},
    onCharacterLongClick: (PreviewCharacterData) -> Unit = {}
) {

    Logger.logMethod()

    var shouldShowSortDialog by remember { mutableStateOf(false) }
    if (shouldShowSortDialog) {
        SortDialog(
            onDismissRequest = { shouldShowSortDialog = false },
            onSortSelected = onSortSelected
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ScreenContent(
        screenState = screenState,
        snackbarHostState = snackbarHostState,
        toolbar = {
            Toolbar(
                title = title,
                onUpButtonClick = onUpButtonClick,
                onEditButtonClick = onEditButtonClick,
                onSortButtonClick = { shouldShowSortDialog = true }
            )
        },
        bottomSheet = {
            val coroutineScope = rememberCoroutineScope()
            BottomSheetContent(
                screenState = screenState,
                isCollapsed = it.isCollapsed,
                onToggleButtonClick = {
                    coroutineScope.launch {
                        if (it.isCollapsed) it.expand()
                        else it.collapse()
                    }
                },
                onPracticeModeSelected = onPracticeModeSelected,
                onShuffleSelected = onShuffleSelected,
                onSelectionOptionSelected = onSelectionOptionSelected,
                onSelectionInputChanged = onSelectionCountInputChanged
            )
        },
        floatingButton = {
            FloatingButton(
                screenState = screenState,
                onClick = {
                    if (screenState is ScreenState.Loaded && screenState.selectedCharacters.isNotEmpty()) {
                        startPractice()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "No characters selected ",
                                withDismissAction = true
                            )
                        }
                    }
                }
            )
        },
        loading = {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        },
        loaded = {
            LoadedState(
                screenState = it,
                onCharacterClick = onCharacterClick,
                onCharacterLongClick = onCharacterLongClick
            )
        }
    )

}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun ScreenContent(
    screenState: ScreenState,
    snackbarHostState: SnackbarHostState,
    toolbar: @Composable () -> Unit,
    bottomSheet: @Composable (BottomSheetState) -> Unit,
    floatingButton: @Composable () -> Unit,
    loading: @Composable () -> Unit,
    loaded: @Composable (ScreenState.Loaded) -> Unit
) {

    val scaffoldState = rememberBottomSheetScaffoldState()

    // TODO replace with material3 version when available + remove Surface composables
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { toolbar() },
        sheetContent = { Surface { bottomSheet(scaffoldState.bottomSheetState) } },
        floatingActionButton = { floatingButton() },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Surface {
            val transition = updateTransition(screenState, label = "")
            transition.Crossfade(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentKey = { it.javaClass.name }
            ) { screenState ->

                when (screenState) {
                    ScreenState.Loading -> loading()
                    is ScreenState.Loaded -> loaded(screenState)
                }

            }
        }

    }

}

@Composable
private fun Toolbar(
    title: String,
    onUpButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    onSortButtonClick: () -> Unit
) {
    SmallTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onUpButtonClick) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        actions = {
            IconButton(onClick = onEditButtonClick) {
                Icon(painterResource(R.drawable.ic_outline_edit_24), null)
            }
            IconButton(onClick = onSortButtonClick) {
                Icon(painterResource(R.drawable.ic_baseline_sort_24), null)
            }
        }
    )
}

@Composable
private fun FloatingButton(
    screenState: ScreenState,
    onClick: () -> Unit
) {
    if (screenState is ScreenState.Loaded) {
        FloatingActionButton(
            onClick = onClick
        ) {
            Icon(painterResource(R.drawable.ic_baseline_play_arrow_24), null)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onCharacterClick: (PreviewCharacterData) -> Unit,
    onCharacterLongClick: (PreviewCharacterData) -> Unit
) {

    Logger.logMethod()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemSize = 50.dp
    val itemsInRow = screenWidth.value.toInt() / itemSize.value.toInt()

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(screenState.characterData.chunked(itemsInRow)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                it.forEach { data ->

                    Box(
                        modifier = Modifier
                            .size(itemSize)
                            .combinedClickable(
                                onClick = { onCharacterClick(data) },
                                onLongClick = { onCharacterLongClick(data) }
                            )
                    ) {

                        Text(
                            text = data.character,
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 36.sp
                        )

                        if (screenState.selectedCharacters.contains(data.character)) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .align(Alignment.TopEnd)
                            )
                        }

                    }

                }

                if (it.size < itemsInRow) {
                    val weight = itemsInRow.toFloat() - it.size
                    Spacer(
                        modifier = Modifier.size(itemSize * weight)
                    )
                }

            }

        }

    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetContent(
    screenState: ScreenState,
    isCollapsed: Boolean,
    onToggleButtonClick: () -> Unit = {},
    onPracticeModeSelected: (PracticeMode) -> Unit = {},
    onShuffleSelected: (Boolean) -> Unit = {},
    onSelectionOptionSelected: (SelectionOption) -> Unit = {},
    onSelectionInputChanged: (String) -> Unit = {}
) {

    Logger.logMethod()

    val loadedState = screenState as? ScreenState.Loaded
    val configuration = loadedState?.selectionConfig ?: SelectionConfiguration.default

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomSheetPeekHeight)
                .clickable(onClick = onToggleButtonClick),
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

            Text(text = "Selected items ($itemsCount)")

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomSheetPeekHeight)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var isDropdownExpanded by remember { mutableStateOf(false) }
            Text(text = "Practice Mode", modifier = Modifier.weight(1f))
            Box {
                TextButton(onClick = { isDropdownExpanded = true }) {
                    Text(text = configuration.practiceMode.title)
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    PracticeMode.values().forEach {
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
                .height(bottomSheetPeekHeight)
                .clickable { onShuffleSelected(!configuration.shuffle) }
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Shuffle", modifier = Modifier.weight(1f))
            Switch(
                checked = configuration.shuffle,
                onCheckedChange = { onShuffleSelected(!configuration.shuffle) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomSheetPeekHeight)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Select characters:")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomSheetPeekHeight)
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
                .height(bottomSheetPeekHeight)
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
                .height(bottomSheetPeekHeight)
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDialog(
    onDismissRequest: () -> Unit,
    onSortSelected: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(SortOptions.FREQUENCY) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Sort") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                SortOptions.values().forEach {
                    val itemClick = { selectedItem = it }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = itemClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = it == selectedItem,
                            onClick = itemClick,
                        )
                        Text(text = it.title)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Apply")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
private fun LoadingPreview() {
    AppTheme {
        val state = ScreenState.Loading
        PracticePreviewScreenUI("Title", state)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
private fun LoadedPreview() {
    AppTheme {
        val state = ScreenState.Loaded(
            practiceId = Random.nextLong(),
            characterData = (0..40).map {
                PreviewCharacterData(
                    character = Random.nextInt().toChar().toString()
                )
            },
            selectionConfig = SelectionConfiguration.default,
            selectedCharacters = sortedSetOf()
        )
        PracticePreviewScreenUI("Title", state)
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetPreview() {
    AppTheme {
        BottomSheetContent(
            screenState = ScreenState.Loaded(
                practiceId = Random.nextLong(),
                characterData = (0..40).map {
                    PreviewCharacterData(
                        character = Random.nextInt().toChar().toString()
                    )
                },
                selectionConfig = SelectionConfiguration.default,
                selectedCharacters = sortedSetOf()
            ),
            isCollapsed = false
        )
    }
}
