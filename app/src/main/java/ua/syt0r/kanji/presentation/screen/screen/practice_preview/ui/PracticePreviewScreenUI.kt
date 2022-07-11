package ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PracticePreviewScreenUI(
    title: String,
    screenState: ScreenState,
    onUpButtonClick: () -> Unit = {},
    onEditButtonClick: () -> Unit = {},
    onSortSelected: (SortConfiguration) -> Unit = {},
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

    var showSortDialog by remember { mutableStateOf(false) }
    if (showSortDialog) {
        screenState as ScreenState.Loaded
        PracticePreviewSortDialog(
            sortConfiguration = screenState.sortConfiguration,
            onSortSelected = {
                onSortSelected(it)
                showSortDialog = false
            },
            onDismissRequest = { showSortDialog = false }
        )
    }

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
            PracticePreviewBottomSheet(
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
                onSortSelected = { showSortDialog = true }
            )
        }
    )

}

@OptIn(
    ExperimentalMaterialApi::class,
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
        sheetPeekHeight = PracticePreviewBottomSheetPeekHeight,
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
            Text(text = "Sorted by")
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = true,
                onClick = { onSortSelected(SortOption.NAME) },
                label = {
                    Text(
                        text = screenState.sortConfiguration
                            .run { "${sortOption.title}: ${if (isDescending) sortOption.descTitle else sortOption.ascTitle}" }
                    )
                },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, null)
                }
            )
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

@Preview
@Composable
private fun LoadingPreview() {
    AppTheme {
        val state = ScreenState.Loading
        PracticePreviewScreenUI("Title", state)
    }
}

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
