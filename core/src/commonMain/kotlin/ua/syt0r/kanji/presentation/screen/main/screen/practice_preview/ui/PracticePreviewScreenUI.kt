package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.syt0r.kanji.presentation.common.MultiplatformBackHandler
import ua.syt0r.kanji.presentation.common.resources.icon.*
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.CustomRippleTheme
import ua.syt0r.kanji.presentation.common.ui.MultiplatformPopup
import ua.syt0r.kanji.presentation.common.ui.PreferredPopupLocation
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.*

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun PracticePreviewScreenUI(
    state: State<ScreenState>,
    onConfigurationUpdated: (PracticePreviewScreenConfiguration) -> Unit,
    onUpButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    selectAllClick: () -> Unit,
    deselectAllClick: () -> Unit,
    onCharacterClick: (String) -> Unit,
    onStartPracticeClick: (PracticeGroup, PracticeConfiguration) -> Unit,
    onDismissMultiselectClick: () -> Unit,
    onEnableMultiselectClick: () -> Unit,
    onGroupClickInMultiselectMode: (PracticeGroup) -> Unit,
    onMultiselectPracticeStart: (MultiselectPracticeConfiguration) -> Unit
) {

    var shouldShowConfigurationDialog by remember { mutableStateOf(false) }
    if (shouldShowConfigurationDialog) {
        PracticePreviewScreenConfigurationDialog(
            configuration = (state.value as ScreenState.Loaded).configuration,
            onDismissRequest = { shouldShowConfigurationDialog = false },
            onApplyConfiguration = {
                shouldShowConfigurationDialog = false
                onConfigurationUpdated(it)
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
    val bottomSheetGroupState: MutableState<PracticeGroup?> = remember { mutableStateOf(null) }

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    if (bottomSheetState.isVisible) {
        MultiplatformBackHandler { coroutineScope.launch { bottomSheetState.hide() } }
    }

    // Updates selected group or hides bottom sheet after review
    LaunchedEffect(Unit) {
        val stateFlow = snapshotFlow { state.value }.filterIsInstance<ScreenState.Loaded>()
        val indexFlow = snapshotFlow { selectedGroupIndexState.value }.filterNotNull()
        stateFlow.combine(indexFlow) { loadedState, index -> loadedState to index }
            .collectLatest { (loadedState, index) ->
                val selectedGroup = loadedState.groups.find { it.index == index }
                if (selectedGroup != null) {
                    bottomSheetGroupState.value = selectedGroup
                } else {
                    bottomSheetState.hide()
                }
            }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Surface {
                val practiceTypeState = remember {
                    derivedStateOf {
                        state.value.let { it as? ScreenState.Loaded }
                            ?.configuration
                            ?.practiceType
                            ?: PracticeType.Writing
                    }
                }
                BottomSheetContent(
                    practiceTypeState = practiceTypeState,
                    practiceGroupState = bottomSheetGroupState,
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
                    configurationButtonClick = { shouldShowConfigurationDialog = true },
                    selectAllClick = selectAllClick,
                    deselectAllClick = deselectAllClick
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                val stateNotLoadedMessage = resolveString {
                    practicePreview.multiselectDataNotLoaded
                }
                val noGroupsSelectedMessage = resolveString {
                    practicePreview.multiselectNoSelected
                }

                FloatingActionButtonSection(
                    state = state,
                    fabLayoutCoordinates = fabLayoutCoordinates,
                    onStartMultiselectMode = {
                        val canStartMultiselect = state.value is ScreenState.Loaded
                        if (canStartMultiselect) {
                            onEnableMultiselectClick()
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    stateNotLoadedMessage,
                                    withDismissAction = true
                                )
                            }
                        }
                    },
                    onConfigureMultiselectPractice = {
                        val canShowDialog = (state.value as? ScreenState.Loaded)
                            ?.selectedGroupIndexes
                            ?.isNotEmpty() == true
                        if (canShowDialog) {
                            shouldShowMultiselectPracticeStartDialog = true
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    noGroupsSelectedMessage,
                                    withDismissAction = true
                                )
                            }
                        }
                    }
                )

            }
        ) { paddingValues ->

            val transition = updateTransition(targetState = state.value, label = "State Transition")
            transition.AnimatedContent(
                contentKey = { it::class },
                transitionSpec = { fadeIn() with fadeOut() },
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
                            },
                            onConfigurationOptionClick = { shouldShowConfigurationDialog = true }
                        )

                        if (screenState.isMultiselectEnabled) {
                            MultiplatformBackHandler(onBack = onDismissMultiselectClick)
                        }
                    }
                }

            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    state: State<ScreenState>,
    upButtonClick: () -> Unit,
    dismissMultiSelectButtonClick: () -> Unit,
    editButtonClick: () -> Unit,
    configurationButtonClick: () -> Unit,
    selectAllClick: () -> Unit,
    deselectAllClick: () -> Unit
) {
    TopAppBar(
        title = { ToolbarTitle(state) },
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
            ToolbarActions(
                state = state,
                editButtonClick = editButtonClick,
                configurationButtonClick = configurationButtonClick,
                selectAllClick = selectAllClick,
                deselectAllClick = deselectAllClick
            )
        }
    )
}

@Composable
private fun ToolbarTitle(state: State<ScreenState>) {
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
            resolveString { practicePreview.multiselectTitle(selectedGroupIndexes.size) }
        } else {
            title
        }

        Text(text = text)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ToolbarActions(
    state: State<ScreenState>,
    editButtonClick: () -> Unit,
    configurationButtonClick: () -> Unit,
    selectAllClick: () -> Unit,
    deselectAllClick: () -> Unit
) {
    var isMultiselectMode by rememberSaveable { mutableStateOf(false) }
    val isLoadingState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { state.value }.collect {
            when (it) {
                ScreenState.Loading -> {
                    isLoadingState.value = true
                }
                is ScreenState.Loaded -> {
                    isLoadingState.value = false
                    isMultiselectMode = it.isMultiselectEnabled
                }
            }
        }
    }

    AnimatedContent(targetState = isMultiselectMode) {
        Row {
            if (it) {
                IconButton(
                    onClick = deselectAllClick
                ) {
                    Icon(ExtraIcons.DeselectAll, null)
                }
                IconButton(
                    onClick = selectAllClick
                ) {
                    Icon(ExtraIcons.SelectAll, null)
                }
            } else {
                IconButton(
                    onClick = editButtonClick
                ) {
                    Icon(Icons.Default.Edit, null)
                }
                IconButton(
                    onClick = configurationButtonClick,
                    enabled = isLoadingState.value.not()
                ) {
                    Icon(Icons.Default.Settings, null)
                }
            }
        }
    }
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
                        ExtraIcons.RadioButtonChecked,
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
    onGroupClick: (PracticeGroup) -> Unit,
    onConfigurationOptionClick: () -> Unit
) {

    if (screenState.groups.isEmpty()) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            ConfigurationIndicatorRow(
                configuration = screenState.configuration,
                onClick = onConfigurationOptionClick
            )
            Text(
                text = resolveString { practicePreview.emptyListMessage },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentSize()
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .wrapContentSize(Alignment.TopCenter)
    ) {

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            ConfigurationIndicatorRow(
                configuration = screenState.configuration,
                onClick = onConfigurationOptionClick
            )
        }

        items(
            items = screenState.groups,
            key = { it.index }
        ) { group ->

            PracticeGroup(
                group = group,
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
            )

        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {

            val screenDensity = LocalDensity.current.density

            val spacerHeight = fabLayoutCoordinates.value
                ?.let {
                    val containerHeight = it.findRootCoordinates().size.height
                    val fabTopPos = it.boundsInRoot().top
                    (containerHeight - fabTopPos) / screenDensity + 16
                }
                ?.dp
                ?: 16.dp

            Spacer(modifier = Modifier.height(spacerHeight))
        }

    }
}

@Composable
private fun CharacterReviewState.toColor(): Color = when (this) {
    CharacterReviewState.RecentlyReviewed -> MaterialTheme.extraColorScheme.success
    CharacterReviewState.NeedReview -> MaterialTheme.extraColorScheme.outdated
    else -> MaterialTheme.colorScheme.surfaceVariant
}

private enum class GroupItemState { Default, Selected, Unselected }

@Composable
private fun PracticeGroup(
    group: PracticeGroup,
    state: GroupItemState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .background(group.reviewState.toColor(), CircleShape)
                .size(8.dp)
        )

        Text(
            text = resolveString {
                practicePreview.listGroupTitle(
                    group.index,
                    group.items.joinToString("") { it.character }
                )
            },
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                // TODO check when new font api is stable, currently LineHeightStyle.Alignment.Center
                //  with disabled font paddings doesn't help
                .padding(bottom = 1.dp),
            overflow = TextOverflow.Ellipsis,
        )

        if (state != GroupItemState.Default) {
            Icon(
                imageVector = if (state == GroupItemState.Selected) ExtraIcons.RadioButtonChecked
                else ExtraIcons.RadioButtonUnchecked,
                contentDescription = null,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

    }

}

@Composable
fun BottomSheetContent(
    practiceTypeState: State<PracticeType>,
    practiceGroupState: State<PracticeGroup?>,
    onCharacterClick: (String) -> Unit,
    onStudyClick: (PracticeGroup, PracticeConfiguration) -> Unit
) {

    val practiceType by practiceTypeState
    val practiceGroup by practiceGroupState

    var practiceConfiguration by remember(practiceType to practiceGroup) {
        val defaultPracticeConfiguration = when (practiceType) {
            PracticeType.Writing -> PracticeConfiguration.Writing(
                isStudyMode = practiceGroup?.summary?.firstReviewDate == null,
                shuffle = true
            )
            PracticeType.Reading -> PracticeConfiguration.Reading(true)
        }
        mutableStateOf(defaultPracticeConfiguration)
    }

    var shouldShowConfigDialog by remember { mutableStateOf(false) }
    if (shouldShowConfigDialog) {
        PracticePreviewStudyOptionsDialog(
            configuration = practiceConfiguration,
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
        when (val group = practiceGroup) {
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
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = resolveString { practicePreview.detailsGroupTitle(group.index) },
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 1.dp) // TODO text alignment api
            )

            var hintDropdownShown by remember { mutableStateOf(false) }

            val dotColor = group.reviewState.toColor()
            val rippleTheme = remember(dotColor) { CustomRippleTheme { dotColor } }

            CompositionLocalProvider(LocalRippleTheme provides rippleTheme) {
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clip(CircleShape)
                        .clickable { hintDropdownShown = true }
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                            .align(Alignment.Center)
                    )

                    MultiplatformPopup(
                        expanded = hintDropdownShown,
                        onDismissRequest = { hintDropdownShown = false },
                        preferredPopupLocation = PreferredPopupLocation.Top
                    ) {
                        Text(
                            text = resolveString {
                                when (group.reviewState) {
                                    CharacterReviewState.RecentlyReviewed -> {
                                        practicePreview.reviewStateRecently
                                    }
                                    CharacterReviewState.NeedReview -> {
                                        practicePreview.reviewStateNeedReview
                                    }
                                    CharacterReviewState.NeverReviewed -> {
                                        practicePreview.reviewStateNever
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

            }

        }

        Text(
            text = resolveString {
                practicePreview.firstTimeReviewMessage(group.summary.firstReviewDate)
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Text(
            text = resolveString {
                practicePreview.lastTimeReviewMessage(group.summary.lastReviewDate)
            },
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

                val reviewState = when (practiceConfiguration) {
                    is PracticeConfiguration.Writing -> it.writingSummary.state
                    is PracticeConfiguration.Reading -> it.readingSummary.state
                }

                Text(
                    text = it.character,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, reviewState.toColor(), MaterialTheme.shapes.medium)
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

            val shuffleMessage = resolveString {
                when {
                    practiceConfiguration.shuffle -> practicePreview.detailsConfigShuffle
                    else -> practicePreview.detailsConfigNoShuffle
                }
            }

            val textConfigurations = resolveString {
                when (practiceConfiguration) {
                    is PracticeConfiguration.Writing -> {
                        val studyMessage = when {
                            practiceConfiguration.isStudyMode -> {
                                practicePreview.detailsConfigStudy
                            }
                            else -> {
                                practicePreview.detailsConfigReview
                            }
                        }
                        listOf(studyMessage, shuffleMessage)
                    }
                    is PracticeConfiguration.Reading -> listOf(shuffleMessage)
                }
            }

            Text(
                text = textConfigurations.joinToString().capitalize(Locale.current),
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilledTonalButton(
                onClick = onStartClick
            ) {
                Text(text = resolveString { practicePreview.detailsPracticeButton })
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigurationIndicatorRow(
    configuration: PracticePreviewScreenConfiguration,
    onClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
    ) {
        FilterChip(
            selected = true,
            onClick = onClick,
            modifier = Modifier.wrapContentSize(Alignment.CenterStart),
            label = { Text(resolveString(configuration.practiceType.titleResolver)) },
            trailingIcon = { Icon(configuration.practiceType.imageVector, null) }
        )
        FilterChip(
            selected = true,
            onClick = onClick,
            modifier = Modifier.wrapContentSize(Alignment.CenterStart),
            label = { Text(resolveString(configuration.filterOption.titleResolver)) },
            trailingIcon = { Icon(configuration.filterOption.imageVector, null) }
        )
        FilterChip(
            selected = true,
            onClick = onClick,
            modifier = Modifier.wrapContentSize(Alignment.CenterStart),
            label = { Text(resolveString(configuration.sortOption.titleResolver)) },
            trailingIcon = {
                Icon(
                    imageVector = configuration.sortOption.imageVector,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = if (configuration.isDescending) 90f else 270f
                    }
                )
            }
        )
    }

}
