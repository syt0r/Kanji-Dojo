package ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwitchDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
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
import java.time.LocalDateTime
import kotlin.random.Random

private val bottomSheetPeekHeight = 56.dp
private val expandableBottomSheetContentItemHeight = bottomSheetPeekHeight
private const val expandableBottomSheetContentItems = 6

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PracticePreviewScreenUI(
    title: String,
    screenState: ScreenState,
    onUpButtonClick: () -> Unit = {},
    onEditButtonClick: () -> Unit = {},
    onSortSelected: (SortOption) -> Unit = {},
    onPracticeModeSelected: (PracticeMode) -> Unit = {},
    onShuffleSelected: (Boolean) -> Unit = {},
    onSelectionOptionSelected: (SelectionOption) -> Unit = {},
    onSelectionCountInputChanged: (String) -> Unit = {},
    startPractice: () -> Unit = {},
    onCharacterClick: (PreviewCharacterData) -> Unit = {},
    onCharacterLongClick: (PreviewCharacterData) -> Unit = {}
) {

    Logger.logMethod()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ScreenContent(
        screenState = screenState,
        snackbarHostState = snackbarHostState,
        toolbar = {
            Toolbar(
                title = title,
                onUpButtonClick = onUpButtonClick,
                onEditButtonClick = onEditButtonClick
            )
        },
        bottomSheet = {
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
                onCharacterLongClick = onCharacterLongClick,
                onSortSelected = onSortSelected
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
    onEditButtonClick: () -> Unit
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
    onCharacterLongClick: (PreviewCharacterData) -> Unit,
    onSortSelected: (SortOption) -> Unit,
) {

    Logger.logMethod()

    Column(Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            SortOption.values().forEach {
                key(it.name) {
                    SortButton(
                        isSelected = it == screenState.sortConfiguration.sortOption,
                        text = it.title,
                        isDescending = screenState.sortConfiguration.isDescending,
                        onClick = { onSortSelected(it) }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val itemSize = 50.dp
        val itemsInRow = screenWidth.value.toInt() / itemSize.value.toInt()
        val listItems = screenState.characterData.chunked(itemsInRow)

        LazyColumn(modifier = Modifier.weight(1f)) {

            items(listItems) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    it.forEach { data ->

                        key(data.character) {

                            Character(
                                character = data.character,
                                isSelected = screenState.selectedCharacters.contains(data.character),
                                modifier = Modifier
                                    .size(itemSize)
                                    .combinedClickable(
                                        onClick = { onCharacterClick(data) },
                                        onLongClick = { onCharacterLongClick(data) }
                                    )
                            )

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
}

@Composable
private fun SortButton(
    isSelected: Boolean,
    text: String,
    isDescending: Boolean,
    onClick: () -> Unit
) {

    val transition = updateTransition(targetState = isSelected, label = "Sort Button Transition")
    val backgroundColor by transition.animateColor(label = "Background Color") { isSelected ->
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surface
    }
    val textColor by transition.animateColor(label = "Text Color") { isSelected ->
        if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(backgroundColor)
            .animateContentSize()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides textColor) {
            Text(text = text, style = MaterialTheme.typography.titleSmall)
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_sort_24),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        if (!isDescending) scaleY = -1f
                    }
                )
            }
        }

    }

}

@Composable
private fun Character(
    character: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
    ) {

        Text(
            text = character,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 36.sp
        )

        if (isSelected) {
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

        Column(
            Modifier
                .fillMaxWidth()
                .height(expandableBottomSheetContentItemHeight * expandableBottomSheetContentItems)
                .verticalScroll(rememberScrollState())
        ) {

            if (loadedState != null) {
                ExpandableBottomSheetContent(
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
private fun ExpandableBottomSheetContent(
    configuration: SelectionConfiguration,
    onPracticeModeSelected: (PracticeMode) -> Unit = {},
    onShuffleSelected: (Boolean) -> Unit = {},
    onSelectionOptionSelected: (SelectionOption) -> Unit = {},
    onSelectionInputChanged: (String) -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(expandableBottomSheetContentItemHeight)
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
            .height(expandableBottomSheetContentItemHeight)
            .clickable { onShuffleSelected(!configuration.shuffle) }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Shuffle", modifier = Modifier.weight(1f))
        // TODO replace when material3 version if fixed
        androidx.compose.material.Switch(
            checked = configuration.shuffle,
            onCheckedChange = { onShuffleSelected(!configuration.shuffle) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(expandableBottomSheetContentItemHeight)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Select characters:")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(expandableBottomSheetContentItemHeight)
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
            .height(expandableBottomSheetContentItemHeight)
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
            .height(expandableBottomSheetContentItemHeight)
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
                    character = Random.nextInt().toChar().toString(),
                    frequency = 0,
                    lastReviewTime = LocalDateTime.MIN
                )
            },
            selectionConfiguration = SelectionConfiguration.default,
            sortConfiguration = SortConfiguration.default,
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
