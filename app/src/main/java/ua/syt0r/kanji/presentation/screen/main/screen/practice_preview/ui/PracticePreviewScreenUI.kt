@file:OptIn(ExperimentalMaterial3Api::class)

package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.showSnackbarFlow
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

private const val GroupsInRow = 2
private val GroupDetailsDateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun PracticePreviewScreenUI(
    state: State<ScreenState>,
    onSortSelected: (SortConfiguration) -> Unit = {},
    onUpButtonClick: () -> Unit = {},
    onEditButtonClick: () -> Unit = {},
    onCharacterClick: (String) -> Unit = {},
    onStartPracticeClick: (PracticeGroup, PracticeConfiguration) -> Unit = { _, _ -> },
    onDismissMultiselectClick: () -> Unit = {},
    onEnableMultiselectClick: () -> Unit = {},
    onGroupClickInMultiselectMode: (PracticeGroup) -> Unit = {},
    onMultiselectPracticeStart: (MultiselectPracticeConfiguration) -> Unit = {}
) {

    var shouldShowSortDialog by remember { mutableStateOf(false) }
    if (shouldShowSortDialog) {
        PracticePreviewSortDialog(
            currentSortConfiguration = (state.value as ScreenState.Loaded).sortConfiguration,
            onDismissRequest = { shouldShowSortDialog = false },
            onApplySort = {
                shouldShowSortDialog = false
                onSortSelected(it)
            }
        )
    }

    var shouldShowMultiselectPracticeStartDialog by remember { mutableStateOf(false) }
    if (shouldShowMultiselectPracticeStartDialog) {
        val loadedState = state.value as ScreenState.Loaded
        if (loadedState.selectedGroupIndexes.isNotEmpty()) {
            PracticePreviewMultiselectDialog(
                groups = loadedState.groups,
                selectedGroupIndexes = loadedState.selectedGroupIndexes,
                onDismissRequest = { shouldShowMultiselectPracticeStartDialog = false },
                onStartClick = onMultiselectPracticeStart
            )
        } else {
            shouldShowMultiselectPracticeStartDialog = false
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val selectedGroupIndexState = rememberSaveable { mutableStateOf<Int?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    if (bottomSheetState.isVisible) {
        BackHandler { coroutineScope.launch { bottomSheetState.hide() } }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Surface {
                BottomSheetContent(
                    selectedGroupIndexState = selectedGroupIndexState,
                    state = state,
                    onCharacterClick = onCharacterClick,
                    onStudyClick = { practiceGroup, practiceConfiguration ->
                        onStartPracticeClick(practiceGroup, practiceConfiguration)
                    }
                )
            }
        }
    ) {

        val fabLayoutCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            topBar = {
                Toolbar(
                    state = state,
                    upButtonClick = onUpButtonClick,
                    dismissMultiSelectButtonClick = onDismissMultiselectClick,
                    editButtonClick = onEditButtonClick,
                    sortButtonClick = { shouldShowSortDialog = true }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {

                val stateNotLoadedMessage =
                    stringResource(R.string.practice_preview_multiselect_not_loaded_message)
                val noGroupsSelectedMessage =
                    stringResource(R.string.practice_preview_multiselect_no_selection_message)

                FloatingActionButtonSection(
                    state = state,
                    fabLayoutCoordinates = fabLayoutCoordinates,
                    onStartMultiselectMode = {
                        val canStartMultiselect = state.value is ScreenState.Loaded
                        if (canStartMultiselect) {
                            onEnableMultiselectClick()
                        } else {
                            snackbarHostState.showSnackbarFlow(
                                stateNotLoadedMessage,
                                withDismissAction = true
                            ).launchIn(coroutineScope)
                        }
                    },
                    onConfigureMultiselectPractice = {
                        val canShowDialog = (state.value as? ScreenState.Loaded)
                            ?.selectedGroupIndexes
                            ?.isNotEmpty() == true
                        if (canShowDialog) {
                            shouldShowMultiselectPracticeStartDialog = true
                        } else {
                            snackbarHostState.showSnackbarFlow(
                                noGroupsSelectedMessage,
                                withDismissAction = true
                            ).launchIn(coroutineScope)
                        }
                    }
                )

            }
        ) { paddingValues ->

            val transition = updateTransition(targetState = state.value, label = "State Transition")
            transition.AnimatedContent(
                contentKey = { it::class },
                transitionSpec = { ContentTransform(fadeIn(), fadeOut()) },
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
                            fabLayoutCoordinates = fabLayoutCoordinates,
                            onGroupClick = {
                                if (screenState.isMultiselectEnabled) {
                                    onGroupClickInMultiselectMode(it)
                                } else {
                                    selectedGroupIndexState.value = it.index
                                    coroutineScope.launch { bottomSheetState.show() }
                                }
                            }
                        )
                    }
                }

            }

        }

    }

}

@Composable
private fun Toolbar(
    state: State<ScreenState>,
    upButtonClick: () -> Unit,
    dismissMultiSelectButtonClick: () -> Unit,
    editButtonClick: () -> Unit,
    sortButtonClick: () -> Unit
) {
    TopAppBar(
        title = {

            var cachedTitleData: Triple<String, Boolean, Set<Int>>? by remember {
                mutableStateOf(null)
            }

            LaunchedEffect(state.value) {
                val updatedTitleData = state.value
                    .let { it as? ScreenState.Loaded }
                    ?.run { Triple(title, isMultiselectEnabled, selectedGroupIndexes) }

                if (updatedTitleData != null && updatedTitleData != cachedTitleData) {
                    cachedTitleData = updatedTitleData
                }
            }

            cachedTitleData?.let { (title, isMultiselectEnabled, selectedGroupIndexes) ->

                val text = if (isMultiselectEnabled) {
                    stringResource(
                        R.string.practice_preview_multiselect_title,
                        selectedGroupIndexes.size
                    )
                } else {
                    title
                }

                Text(text = text)

            }
        },
        navigationIcon = {
            val shouldShowMultiselectDismissButton by remember {
                derivedStateOf {
                    state.value
                        .let { it as? ScreenState.Loaded }
                        ?.isMultiselectEnabled == true
                }
            }
            if (shouldShowMultiselectDismissButton) {
                IconButton(
                    onClick = dismissMultiSelectButtonClick
                ) {
                    Icon(Icons.Default.Close, null)
                }
            } else {
                IconButton(
                    onClick = upButtonClick
                ) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }

        },
        actions = {
            IconButton(
                onClick = editButtonClick
            ) {
                Icon(painterResource(R.drawable.ic_outline_edit_24), null)
            }
            val isSortButtonEnabled by remember {
                derivedStateOf { state.value is ScreenState.Loaded }
            }
            IconButton(
                onClick = sortButtonClick,
                enabled = isSortButtonEnabled
            ) {
                Icon(painterResource(R.drawable.ic_baseline_sort_24), null)
            }
        }
    )
}

@Composable
private fun LoadingState() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FloatingActionButtonSection(
    state: State<ScreenState>,
    fabLayoutCoordinates: MutableState<LayoutCoordinates?>,
    onStartMultiselectMode: () -> Unit,
    onConfigureMultiselectPractice: () -> Unit
) {

    val isInMultiselectMode by remember {
        derivedStateOf {
            state.value.let { it as? ScreenState.Loaded }?.isMultiselectEnabled == true
        }
    }

    FloatingActionButton(
        onClick = {
            if (isInMultiselectMode) {
                onConfigureMultiselectPractice()
            } else {
                onStartMultiselectMode()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.BottomEnd)
            .onGloballyPositioned { fabLayoutCoordinates.value = it },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {

        AnimatedContent(
            targetState = isInMultiselectMode,
            transitionSpec = { fadeIn(tween(150, 150)) with fadeOut(tween(150)) }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (it) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_radio_button_checked_24),
                        contentDescription = null
                    )
                }
            }
        }

    }
}

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    fabLayoutCoordinates: State<LayoutCoordinates?>,
    onGroupClick: (PracticeGroup) -> Unit
) {

    val rows = remember(screenState.groups) { screenState.groups.chunked(GroupsInRow) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(
            items = rows,
            key = { it.first().index }
        ) { rowItems ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {

                Spacer(modifier = Modifier.width(10.dp))

                rowItems.forEach { group ->
                    GroupItem(
                        index = group.index,
                        text = group.items.joinToString("") { it.character },
                        state = when {
                            screenState.isMultiselectEnabled -> screenState.selectedGroupIndexes
                                .contains(group.index)
                                .let {
                                    if (it) GroupItemState.Selected else GroupItemState.Unselected
                                }
                            else -> GroupItemState.Default
                        },
                        onClick = { onGroupClick(group) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp)
                    )
                }

                if (rowItems.size != GroupsInRow) {
                    Spacer(modifier = Modifier.weight(GroupsInRow.toFloat() - rowItems.size))
                }

            }

        }

        item {

            val screenHeightDp = LocalConfiguration.current.screenHeightDp
            val screenDensity = LocalDensity.current.density

            val spacerHeight = fabLayoutCoordinates.value
                ?.let { screenHeightDp - it.boundsInRoot().top / screenDensity + 16 }
                ?.dp
                ?: 16.dp

            Spacer(modifier = Modifier.height(spacerHeight))
        }

    }

}

private enum class GroupItemState { Default, Selected, Unselected }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupItem(
    index: Int,
    text: String,
    state: GroupItemState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = index.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                    .padding(vertical = 4.dp, horizontal = 10.dp)
                    .wrapContentSize()
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )

            if (state != GroupItemState.Default) {
                Icon(
                    painter = painterResource(
                        id = if (state == GroupItemState.Selected) R.drawable.ic_baseline_radio_button_checked_24
                        else R.drawable.ic_baseline_radio_button_unchecked_24
                    ),
                    contentDescription = null
                )
            }

        }

    }

}

@Composable
fun BottomSheetContent(
    selectedGroupIndexState: State<Int?>,
    state: State<ScreenState>,
    onCharacterClick: (String) -> Unit,
    onStudyClick: (PracticeGroup, PracticeConfiguration) -> Unit
) {

    val groupState: State<PracticeGroup?> = remember {
        derivedStateOf {
            val loadedState = state.value as? ScreenState.Loaded
            val index = selectedGroupIndexState.value

            if (loadedState != null && index != null) {
                loadedState.groups.find { it.index == index }
            } else {
                null
            }
        }
    }

    var shouldShowConfigDialog by remember { mutableStateOf(false) }

    /***
     * Using cache to avoid abrupt animations after returning from practice while bottom sheet is
     * open because group state becomes null for a moment
     */
    var cachedGroup: PracticeGroup? by rememberSaveable { mutableStateOf(groupState.value) }

    LaunchedEffect(groupState.value) {
        val currentGroup = groupState.value
        if (currentGroup != null && cachedGroup != currentGroup) {
            cachedGroup = currentGroup
        }
    }

    var practiceConfiguration by rememberSaveable(cachedGroup) {
        mutableStateOf(
            PracticeConfiguration(
                isStudyMode = cachedGroup?.firstDate == null,
                shuffle = true
            )
        )
    }

    if (shouldShowConfigDialog) {
        PracticePreviewStudyOptionsDialog(
            defaultConfiguration = practiceConfiguration,
            onDismissRequest = { shouldShowConfigDialog = false },
            onApplyConfiguration = {
                shouldShowConfigDialog = false
                practiceConfiguration = it
            }
        )
    }

    Box(
        modifier = Modifier.animateContentSize(tween(100, easing = LinearEasing))
    ) {
        when (val group = cachedGroup) {
            null -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .wrapContentSize()
                )
            }
            else -> {
                PracticeGroupDetails(
                    group = group,
                    practiceConfiguration = practiceConfiguration,
                    onCharacterClick = onCharacterClick,
                    onOptionsClick = { shouldShowConfigDialog = true },
                    onStartClick = { onStudyClick(group, practiceConfiguration) }
                )
            }
        }
    }

}


@Composable
private fun PracticeGroupDetails(
    group: PracticeGroup,
    practiceConfiguration: PracticeConfiguration,
    onCharacterClick: (String) -> Unit = {},
    onOptionsClick: () -> Unit = {},
    onStartClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = stringResource(R.string.practice_preview_group_template, group.index),
                style = MaterialTheme.typography.titleLarge
            )

        }

        val firstDateMessage = group.firstDate?.format(GroupDetailsDateTimeFormat)
            ?: stringResource(R.string.practice_preview_date_never)

        Text(
            text = stringResource(R.string.practice_preview_first_date_template, firstDateMessage),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        val lastDateMessage = group.lastDate?.format(GroupDetailsDateTimeFormat)
            ?: stringResource(R.string.practice_preview_date_never)

        Text(
            text = stringResource(R.string.practice_preview_last_date_template, lastDateMessage),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        val scrollState = remember(group.index) { ScrollState(0) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {

            Spacer(modifier = Modifier.width(20.dp))

            group.items.forEach {

                Text(
                    text = it.character,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(6.dp)
                        )
                        .height(IntrinsicSize.Min)
                        .aspectRatio(1f, true)
                        .clickable { onCharacterClick(it.character) }
                        .padding(8.dp)
                        .wrapContentSize()
                )

                Spacer(modifier = Modifier.width(10.dp))

            }

            Spacer(modifier = Modifier.width(10.dp))

        }

        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onOptionsClick
            ) {
                Icon(Icons.Outlined.Settings, null)
            }

            Spacer(modifier = Modifier.width(8.dp))

            val studyMessage = when {
                practiceConfiguration.isStudyMode -> stringResource(R.string.practice_preview_config_study)
                else -> stringResource(R.string.practice_preview_config_review)
            }

            val shuffleMessage = when {
                practiceConfiguration.shuffle -> stringResource(R.string.practice_preview_config_shuffle)
                else -> stringResource(R.string.practice_preview_config_no_shuffle)
            }

            Text(
                text = stringResource(
                    R.string.practice_preview_config_template,
                    studyMessage,
                    shuffleMessage
                ),
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilledTonalButton(
                onClick = onStartClick
            ) {
                Text(text = stringResource(R.string.practice_preview_practice_start))
            }

        }

    }
}

@Preview
@Composable
private fun LoadedPreview() {
    AppTheme(true) {
        val state = remember {
            mutableStateOf(
                ScreenState.Loaded(
                    title = "Test Practice",
                    sortConfiguration = SortConfiguration.default,
                    groups = (1..9).map {
                        PracticeGroup(
                            index = it,
                            items = (1..6).map { PracticeGroupItem.random() },
                            firstDate = LocalDateTime.now(),
                            lastDate = LocalDateTime.now()
                        )
                    },
                    isMultiselectEnabled = false,
                    selectedGroupIndexes = emptySet()
                )
            )
        }
        PracticePreviewScreenUI(state = state)
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupDetailsPreview() {
    AppTheme {
        PracticeGroupDetails(
            group = PracticeGroup(
                index = Random.nextInt(1, 100),
                items = (1..6).map { PracticeGroupItem.random() },
                firstDate = LocalDateTime.now(),
                lastDate = LocalDateTime.now()
            ),
            practiceConfiguration = PracticeConfiguration(isStudyMode = true, shuffle = true)
        )
    }
}
