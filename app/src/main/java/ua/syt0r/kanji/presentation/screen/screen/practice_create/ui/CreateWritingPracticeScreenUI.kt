package ua.syt0r.kanji.presentation.screen.screen.practice_create.ui

import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.RoundedCircularProgressBar
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.InputProcessingResult
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CreateWritingPracticeScreenUI(
    configuration: CreatePracticeConfiguration,
    screenState: ScreenState,
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

    val isSavingState = screenState is ScreenState.Loaded && screenState.currentDataAction.let {
        it == DataAction.Saving || it == DataAction.SaveCompleted
    }
    var showTitleInputDialog by remember { mutableStateOf(false) }
    if (showTitleInputDialog || isSavingState) {
        screenState as ScreenState.Loaded
        TitleInputDialog(
            action = screenState.currentDataAction,
            initialTitle = screenState.initialPracticeTitle,
            onInputSubmitted = onSaveConfirmed,
            onDismissRequest = { showTitleInputDialog = false },
            onSaveAnimationCompleted = onSaveAnimationCompleted
        )
    }

    val isDeletingState = screenState is ScreenState.Loaded && screenState.currentDataAction.let {
        it == DataAction.Deleting || it == DataAction.DeleteCompleted
    }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    if (showDeleteConfirmationDialog || isDeletingState) {
        screenState as ScreenState.Loaded
        DeleteConfirmationDialog(
            action = screenState.currentDataAction,
            practiceTitle = screenState.initialPracticeTitle!!,
            onDismissRequest = { showDeleteConfirmationDialog = false },
            onDeleteConfirmed = onPracticeDeleteClick,
            onDeleteAnimationCompleted = onDeleteAnimationCompleted
        )
    }

    var unknownEnteredCharacters: Set<String> by remember { mutableStateOf(emptySet()) }
    if (unknownEnteredCharacters.isNotEmpty()) {
        UnknownCharactersDialog(
            onDismissRequest = { unknownEnteredCharacters = emptySet() },
            characters = unknownEnteredCharacters
        )
    }

    Scaffold(
        topBar = {
            Toolbar(
                configuration = configuration,
                screenState = screenState,
                onUpClick = onUpClick,
                onDeleteClick = { showDeleteConfirmationDialog = true }
            )
        },
        floatingActionButton = {
            FloatingButton(
                screenState = screenState,
                onClick = { showTitleInputDialog = true }
            )
        }
    ) { paddingValues ->

        val coroutineScope = rememberCoroutineScope()

        val transition = updateTransition(
            targetState = screenState,
            label = "State Update Transition"
        )
        transition.AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(),
                    initialContentExit = fadeOut()
                )
            },
            contentKey = { it.javaClass.name }
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

@Composable
private fun Toolbar(
    configuration: CreatePracticeConfiguration,
    screenState: ScreenState,
    onUpClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    SmallTopAppBar(
        title = {
            Text(
                if (configuration is CreatePracticeConfiguration.EditExisting) {
                    stringResource(R.string.writing_practice_edit_title)
                } else {
                    stringResource(R.string.writing_practice_create_title)
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
            if (configuration is CreatePracticeConfiguration.EditExisting) {
                IconButton(
                    onClick = onDeleteClick,
                    enabled = screenState is ScreenState.Loaded &&
                            screenState.currentDataAction == DataAction.Loaded
                ) {
                    Icon(Icons.Default.Delete, null)
                }
            }
        }
    )
}

@Composable
private fun FloatingButton(
    screenState: ScreenState,
    onClick: () -> Unit
) {
    val overshootInterpolator = remember { OvershootInterpolator() }
    val anticipateOvershootInterpolator = remember { AnticipateOvershootInterpolator() }

    AnimatedVisibility(
        visible = screenState is ScreenState.Loaded && screenState.characters.isNotEmpty(),
        enter = slideInVertically(
            tween(easing = { overshootInterpolator.getInterpolation(it) })
        ),
        exit = slideOutVertically(
            tween(easing = { anticipateOvershootInterpolator.getInterpolation(it) })
        ),
    ) {
        screenState as ScreenState.Loaded
        val listSize = screenState.characters.size
        ExtendedFloatingActionButton(
            onClick = onClick,
            text = { Text("Save (${listSize} items)") },
            icon = { Icon(painterResource(R.drawable.ic_baseline_save_24), null) }
        )
    }
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
            .padding(horizontal = 24.dp)
    ) {

        CharacterInputField(
            isEnabled = screenState.currentDataAction == DataAction.Loaded,
            onInputSubmit = onInputSubmit
        )

        Spacer(modifier = Modifier.height(24.dp))

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val itemSize = 50.dp
        val itemsInRow = screenWidth.value.toInt() / itemSize.value.toInt()

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

    val enteredText = remember { mutableStateOf("") }

    OutlinedTextField(
        value = enteredText.value,
        onValueChange = { enteredText.value = it },
        maxLines = 1,
        label = { Text("Enter kanji here") },
        shape = CircleShape,
        leadingIcon = {
            IconButton(
                onClick = { enteredText.value = "" }
            ) {
                Icon(Icons.Default.Close, null)
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    onInputSubmit(enteredText.value)
                    enteredText.value = ""
                },
                enabled = isEnabled
            ) {
                Icon(Icons.Default.Search, null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

}

@OptIn(ExperimentalFoundationApi::class)
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
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {

                DropdownMenuItem(
                    text = { Text(text = "Info") },
                    leadingIcon = { Icon(Icons.Default.Info, null) },
                    onClick = {
                        isExpanded = false
                        onInfoClick(character)
                    }
                )

                if (isPendingRemoval) {
                    DropdownMenuItem(
                        text = { Text(text = "Return") },
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
                        text = { Text(text = "Remove") },
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
private fun TitleInputDialog(
    action: DataAction,
    initialTitle: String?,
    onInputSubmitted: (userInput: String) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveAnimationCompleted: () -> Unit
) {

    var input: String by remember { mutableStateOf(initialTitle ?: "") }

    val isSaveCompleted = action == DataAction.SaveCompleted
    if (isSaveCompleted) {
        LaunchedEffect(Unit) {
            delay(600)
            onSaveAnimationCompleted()
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        title = {
            Text(text = "Practice Name")
        },
        text = {
            TextField(
                value = input,
                onValueChange = { input = it },
                isError = input.isEmpty(),
                trailingIcon = {
                    IconButton(onClick = { input = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                enabled = action == DataAction.Loaded
            )
        },
        confirmButton = {
            TextButton(
                enabled = when (action) {
                    DataAction.Loaded -> input.isNotEmpty()
                    else -> false
                },
                onClick = { onInputSubmitted(input) },
                modifier = Modifier.animateContentSize()
            ) {
                Text(text = if (isSaveCompleted) "Done" else "Save")
                if (action == DataAction.Saving) {
                    Spacer(modifier = Modifier.width(4.dp))
                    RoundedCircularProgressBar(strokeWidth = 1.dp, Modifier.size(10.dp))
                }
            }
        }
    )

}

@Composable
private fun DeleteConfirmationDialog(
    action: DataAction,
    practiceTitle: String,
    onDismissRequest: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDeleteAnimationCompleted: () -> Unit,
) {

    val isDeleteCompleted = action == DataAction.DeleteCompleted
    if (isDeleteCompleted) {
        LaunchedEffect(Unit) {
            delay(600)
            onDeleteAnimationCompleted()
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        title = {
            Text(text = "Delete confirmation")
        },
        text = {
            Text(text = "Are you sure you want to delete \"$practiceTitle\" practice?")
        },
        confirmButton = {
            TextButton(
                enabled = action == DataAction.Loaded,
                onClick = onDeleteConfirmed,
                modifier = Modifier.animateContentSize()
            ) {
                Text(text = if (isDeleteCompleted) "Done" else "Delete")
                if (action == DataAction.Saving) {
                    Spacer(modifier = Modifier.width(4.dp))
                    RoundedCircularProgressBar(strokeWidth = 1.dp, Modifier.size(10.dp))
                }
            }
        }
    )

}

@Composable
private fun UnknownCharactersDialog(
    onDismissRequest: () -> Unit,
    characters: Set<String>
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Unknown characters") },
        text = { Text(text = "Characters " + characters.joinToString() + " are not found") },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Close")
            }
        }
    )

}

@Preview
@Composable
private fun CreatePreview() {
    AppTheme {
        CreateWritingPracticeScreenUI(
            configuration = CreatePracticeConfiguration.NewPractice,
            screenState = ScreenState.Loaded(
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
            )
        )
    }
}

@Preview
@Composable
private fun EditPreview() {
    AppTheme {
        CreateWritingPracticeScreenUI(
            configuration = CreatePracticeConfiguration.EditExisting(practiceId = 1),
            screenState = ScreenState.Loaded(
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
            )
        )
    }
}