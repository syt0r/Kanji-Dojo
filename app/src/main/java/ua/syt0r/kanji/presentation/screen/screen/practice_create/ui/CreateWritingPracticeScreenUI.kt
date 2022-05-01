package ua.syt0r.kanji.presentation.screen.screen.practice_create.ui

import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.EnteredKanji
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWritingPracticeScreenUI(
    configuration: CreatePracticeConfiguration,
    screenState: ScreenState,
    onUpClick: () -> Unit = {},
    onPracticeDeleteClick: () -> Unit = {},
    submitKanjiInput: (input: String) -> Unit = {},
    onCharacterInfoClick: (String) -> Unit = {},
    onCharacterDeleteClick: (String) -> Unit = {},
    onChangesConfirmationClick: (title: String) -> Unit = {}
) {

    val isSavingState = screenState is ScreenState.Loaded && screenState.currentDataAction.let {
        it == DataAction.Saving || it == DataAction.SaveCompleted
    }
    var shouldShowTitleInputDialog by remember { mutableStateOf(false) }
    if (shouldShowTitleInputDialog || isSavingState) {
        TitleInputDialog(
            initialTitle = (screenState as ScreenState.Loaded).initialPracticeTitle,
            onInputSubmitted = {
                onChangesConfirmationClick(it)
                shouldShowTitleInputDialog = false
            },
            onCancel = { shouldShowTitleInputDialog = false }
        )
    }

    val isDeletingState = screenState is ScreenState.Loaded && screenState.currentDataAction.let {
        it == DataAction.Deleting || it == DataAction.DeleteCompleted
    }
    var shouldShowDeleteConfirmationDialog by remember { mutableStateOf(false) }
    if (shouldShowDeleteConfirmationDialog || isDeletingState) {
        DeleteConfirmationDialog(
            practiceTitle = (screenState as ScreenState.Loaded).initialPracticeTitle!!,
            screenState = screenState,
            onDismissRequest = { shouldShowDeleteConfirmationDialog = false },
            onConfirmClick = onPracticeDeleteClick
        )
    }

    Scaffold(
        topBar = {
            Toolbar(
                configuration = configuration,
                screenState = screenState,
                onUpClick = onUpClick,
                onDeleteClick = { shouldShowDeleteConfirmationDialog = true }
            )
        },
        floatingActionButton = {
            FloatingButton(
                screenState = screenState,
                onClick = { shouldShowTitleInputDialog = true }
            )
        }
    ) { paddingValues ->

        Crossfade(
            targetState = screenState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> {
                    LoadingState()
                }
                is ScreenState.Loaded -> {
                    LoadedState(
                        screenState = screenState,
                        onUserSubmittedInput = { submitKanjiInput(it) },
                        onInfoClick = {},
                        onDeleteClick = {}
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
        visible = screenState is ScreenState.Loaded && screenState.data.isNotEmpty(),
        enter = slideInVertically(
            tween(easing = { overshootInterpolator.getInterpolation(it) })
        ),
        exit = slideOutVertically(
            tween(easing = { anticipateOvershootInterpolator.getInterpolation(it) })
        ),
    ) {
        screenState as ScreenState.Loaded
        val listSize = screenState.data.size
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
    onUserSubmittedInput: (String) -> Unit,
    onInfoClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
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
                        onUserSubmittedInput(enteredText.value)
                        enteredText.value = ""
                    },
                    enabled = screenState.currentDataAction == DataAction.Loaded
                ) {
                    Icon(Icons.Default.Search, null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val itemSize = 50.dp
        val itemsInRow = screenWidth.value.toInt() / itemSize.value.toInt()

        LazyColumn {

            items(screenState.data.chunked(itemsInRow)) {

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center
                ) {

                    it.forEach {
                        Character(
                            modifier = Modifier.size(itemSize),
                            kanji = it,
                            onInfoClick = {},
                            onDeleteClick = {}
                        )
                    }

                    if (it.size != itemsInRow) {
                        val emptyItems = itemsInRow - it.size
                        Box(modifier = Modifier.width(itemSize * emptyItems))
                    }

                }

            }
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Character(
    modifier: Modifier,
    kanji: EnteredKanji,
    onInfoClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.clickable { isExpanded = true }
    ) {

        Text(
            text = kanji.kanji,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 32.sp
        )

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
                        onInfoClick(kanji.kanji)
                    }
                )

                DropdownMenuItem(
                    text = { Text(text = "Remove") },
                    leadingIcon = { Icon(Icons.Default.Delete, null) },
                    onClick = {
                        isExpanded = false
                        onDeleteClick(kanji.kanji)
                    }
                )

            }
        }

    }


}

@Composable
private fun TitleInputDialog(
    initialTitle: String?,
    onInputSubmitted: (userInput: String) -> Unit,
    onCancel: () -> Unit
) {

    var input: String by remember { mutableStateOf(initialTitle ?: "") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "Practice Name")
        },
        text = {
            TextField(value = input, onValueChange = { input = it })
        },
        confirmButton = {
            TextButton(onClick = { onInputSubmitted(input) }) {
                Text(text = "Save")
            }
        }
    )

}

@Composable
private fun DeleteConfirmationDialog(
    practiceTitle: String,
    screenState: ScreenState.Loaded,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {

    Logger.logMethod()
    val isDismissable = screenState.currentDataAction != DataAction.Deleting

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = isDismissable,
            dismissOnClickOutside = isDismissable
        )
    ) {

        Surface(shape = MaterialTheme.shapes.extraLarge) {

            Crossfade(targetState = screenState) { screenState ->

                when (screenState.currentDataAction) {
                    DataAction.Deleting -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                    DataAction.DeleteCompleted -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text("Done", Modifier.align(Alignment.Center))
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Delete confirmation",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Are you sure you want to delete \"$practiceTitle\" practice?")
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), Arrangement.End) {
                                TextButton(onClick = onConfirmClick) {
                                    Text(text = "Delete")
                                }
                            }
                        }
                    }
                }

            }

        }

    }

}

@Preview
@Composable
private fun CreatePreview() {
    AppTheme {
        CreateWritingPracticeScreenUI(
            configuration = CreatePracticeConfiguration.NewPractice,
            screenState = ScreenState.Loaded(
                initialPracticeTitle = null,
                data = (2..10)
                    .map {
                        EnteredKanji(
                            kanji = Char(
                                Random.nextInt(Char.MIN_VALUE.code, Char.MAX_VALUE.code)
                            ).toString(),
                            isKnown = Random.nextBoolean()
                        )
                    }
                    .toSet(),
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
                data = (2..10)
                    .map {
                        EnteredKanji(
                            kanji = Char(
                                Random.nextInt(Char.MIN_VALUE.code, Char.MAX_VALUE.code)
                            ).toString(),
                            isKnown = Random.nextBoolean()
                        )
                    }
                    .toSet(),
                currentDataAction = DataAction.Loaded
            )
        )
    }
}