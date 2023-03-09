package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.showSnackbarFlow
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.CustomDropdownMenu
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji.randomKanji
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.InputProcessingResult
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CreateWritingPracticeScreenUI(
    configuration: MainDestination.CreatePractice,
    state: State<ScreenState>,
    onUpClick: () -> Unit = {},
    onPracticeDeleteClick: () -> Unit = {},
    onDeleteAnimationCompleted: () -> Unit = {},
    onCharacterInfoClick: (String) -> Unit = {},
    onCharacterDeleteClick: (String) -> Unit = {},
    onCharacterRemovalCancel: (String) -> Unit = {},
    onSaveConfirmed: (title: String) -> Unit = {},
    onSaveAnimationCompleted: () -> Unit = {},
    submitKanjiInput: suspend (input: String) -> InputProcessingResult = { TODO() }
) {

    var showTitleInputDialog by remember { mutableStateOf(false) }
    if (showTitleInputDialog) {
        val saveWritingPracticeDialogData = remember {
            derivedStateOf {
                val currentState = state.value as ScreenState.Loaded
                SaveWritingPracticeDialogData(
                    initialTitle = currentState.initialPracticeTitle,
                    dataAction = currentState.currentDataAction
                )
            }
        }
        SaveWritingPracticeDialog(
            state = saveWritingPracticeDialogData,
            onInputSubmitted = onSaveConfirmed,
            onDismissRequest = { showTitleInputDialog = false },
            onSaveAnimationCompleted = onSaveAnimationCompleted
        )
    }

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    if (showDeleteConfirmationDialog) {
        val deleteConfirmationDialogData = remember {
            derivedStateOf {
                val currentState = state.value as ScreenState.Loaded
                DeleteWritingPracticeDialogData(
                    practiceTitle = currentState.initialPracticeTitle!!,
                    currentAction = currentState.currentDataAction
                )
            }
        }
        DeleteWritingPracticeDialog(
            state = deleteConfirmationDialogData,
            onDismissRequest = { showDeleteConfirmationDialog = false },
            onDeleteConfirmed = onPracticeDeleteClick,
            onDeleteAnimationCompleted = onDeleteAnimationCompleted
        )
    }

    var unknownEnteredCharacters: Set<String> by remember { mutableStateOf(emptySet()) }
    if (unknownEnteredCharacters.isNotEmpty()) {
        UnknownCharactersDialog(
            characters = unknownEnteredCharacters,
            onDismissRequest = { unknownEnteredCharacters = emptySet() }
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            Toolbar(
                configuration = configuration,
                state = state,
                onUpClick = onUpClick,
                onDeleteClick = { showDeleteConfirmationDialog = true }
            )
        },
        floatingActionButton = {
            val snackbarMessage = stringResource(R.string.practice_create_not_ready_message)
            FloatingActionButton(
                onClick = {
                    val isLoaded = state.value.let {
                        it is ScreenState.Loaded && it.currentDataAction == DataAction.Loaded
                    }
                    if (isLoaded) {
                        showTitleInputDialog = true
                    } else {
                        snackbarHostState.showSnackbarFlow(
                            snackbarMessage,
                            withDismissAction = true
                        ).launchIn(coroutineScope)
                    }
                },
                content = { Icon(painterResource(R.drawable.ic_baseline_save_24), null) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        val transition = updateTransition(
            targetState = state.value,
            label = "State Update Transition"
        )
        transition.AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            transitionSpec = { fadeIn() with fadeOut() },
            contentKey = { it::class }
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> {
                    LoadingState()
                }
                is ScreenState.Loaded -> {
                    LoadedState(
                        screenState = screenState,
                        onInputSubmit = {
                            coroutineScope.launch {
                                unknownEnteredCharacters = submitKanjiInput(it).unknownCharacters
                            }
                        },
                        onInfoClick = onCharacterInfoClick,
                        onDeleteClick = onCharacterDeleteClick,
                        onDeleteCancel = onCharacterRemovalCancel
                    )
                }
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    configuration: MainDestination.CreatePractice,
    state: State<ScreenState>,
    onUpClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                if (configuration is MainDestination.CreatePractice.EditExisting) {
                    stringResource(R.string.practice_create_edit_existing_title)
                } else {
                    stringResource(R.string.practice_create_new_practice_title)
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (configuration is MainDestination.CreatePractice.EditExisting) {
                val isEditEnabled = remember {
                    derivedStateOf {
                        val currentState = state.value
                        currentState is ScreenState.Loaded &&
                                currentState.currentDataAction == DataAction.Loaded
                    }
                }
                IconButton(
                    onClick = onDeleteClick,
                    enabled = isEditEnabled.value
                ) {
                    Icon(Icons.Default.Delete, null)
                }
            }
        }
    )
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onInputSubmit: (String) -> Unit,
    onInfoClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDeleteCancel: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        CharacterInputField(
            isEnabled = screenState.currentDataAction == DataAction.Loaded,
            onInputSubmit = onInputSubmit
        )

        Spacer(modifier = Modifier.height(24.dp))

        val availableWidth = LocalConfiguration.current.screenWidthDp.dp - 20.dp
        val itemSize = 50.dp
        val itemsInRow = availableWidth.value.toInt() / itemSize.value.toInt()

        LazyColumn {

            items(screenState.characters.chunked(itemsInRow)) {

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center
                ) {

                    it.forEach {
                        key(it) {
                            Character(
                                character = it,
                                isPendingRemoval = screenState.charactersPendingForRemoval
                                    .contains(it),
                                modifier = Modifier.size(itemSize),
                                onInfoClick = onInfoClick,
                                onDeleteClick = onDeleteClick,
                                onDeleteCancel = onDeleteCancel
                            )
                        }
                    }

                    if (it.size != itemsInRow) {
                        val emptyItems = itemsInRow - it.size
                        Box(modifier = Modifier.width(itemSize * emptyItems))
                    }

                }

            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }

        }

    }

}

@Composable
private fun CharacterInputField(
    isEnabled: Boolean,
    onInputSubmit: (String) -> Unit
) {

    var enteredText by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = { enteredText = "" }
        ) {
            Icon(Icons.Default.Close, null)
        }

        Box(modifier = Modifier.weight(1f)) {

            var isInputFocused by remember { mutableStateOf(false) }

            BasicTextField(
                value = enteredText,
                onValueChange = { enteredText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isInputFocused = it.isFocused },
                maxLines = 1,
                singleLine = true,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(color),
                textStyle = TextStyle.Default.copy(color)
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = !isInputFocused && enteredText.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = stringResource(R.string.practice_create_search_hint),
                    style = MaterialTheme.typography.titleMedium
                )
            }

        }

        IconButton(
            onClick = {
                onInputSubmit(enteredText)
                enteredText = ""
            },
            enabled = isEnabled
        ) {
            Icon(Icons.Default.Search, null)
        }

    }

}

@Composable
private fun Character(
    character: String,
    isPendingRemoval: Boolean,
    modifier: Modifier = Modifier,
    onInfoClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDeleteCancel: (String) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.clickable { isExpanded = true }
    ) {

        Text(
            text = character,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 32.sp
        )

        AnimatedVisibility(
            visible = isPendingRemoval,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                Icons.Default.Close,
                null,
                modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (isExpanded) {
            CustomDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {

                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.practice_create_item_action_info)) },
                    leadingIcon = { Icon(Icons.Default.Info, null) },
                    onClick = {
                        isExpanded = false
                        onInfoClick(character)
                    }
                )

                if (isPendingRemoval) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.practice_create_item_action_return)) },
                        leadingIcon = {
                            Icon(painterResource(R.drawable.ic_baseline_restore_24), null)
                        },
                        onClick = {
                            isExpanded = false
                            onDeleteCancel(character)
                        }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.practice_create_item_action_remove)) },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = {
                            isExpanded = false
                            onDeleteClick(character)
                        }
                    )
                }

            }
        }

    }


}

@Composable
private fun UnknownCharactersDialog(
    characters: Set<String>,
    onDismissRequest: () -> Unit = {}
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.practice_create_unknown_dialog_title)) },
        text = {
            Text(
                text = stringResource(
                    R.string.practice_create_unknown_dialog_message,
                    characters.joinToString()
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(R.string.practice_create_unknown_dialog_button))
            }
        }
    )

}

@Preview
@Composable
private fun CreatePreview() {
    AppTheme {
        CreateWritingPracticeScreenUI(
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
        CreateWritingPracticeScreenUI(
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

@Preview
@Composable
private fun UnknownCharactersDialogPreview() {
    AppTheme {
        UnknownCharactersDialog(
            characters = (0 until 5).map { randomKanji() }.toSet()
        )
    }
}