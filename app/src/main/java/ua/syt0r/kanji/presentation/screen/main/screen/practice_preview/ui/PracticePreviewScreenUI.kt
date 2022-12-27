@file:OptIn(ExperimentalMaterial3Api::class)

package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.CustomDropdownMenu
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
    title: String,
    state: State<ScreenState>,
    onSortSelected: (SortConfiguration) -> Unit = {},
    navigateBack: () -> Unit = {},
    navigateToEdit: () -> Unit = {},
    navigateToCharacterInfo: (String) -> Unit = {},
    navigateToPractice: (PracticeGroup, PracticeConfiguration) -> Unit = { _, _ -> }
) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var selectedGroup by rememberSaveable(Unit) { mutableStateOf<PracticeGroup?>(null) }

    LaunchedEffect(state.value) {
        selectedGroup?.let { practiceGroup ->
            state.value.let { it as? ScreenState.Loaded }
                ?.groups
                ?.find { it.index == practiceGroup.index }
                ?.takeIf { it != practiceGroup }
                ?.let {
                    Logger.d("Updating outdated state group")
                    selectedGroup = it
                }
        }
    }

    var shouldShowSortDialog by remember { mutableStateOf(false) }
    if (shouldShowSortDialog) {
        SortDialog(
            currentSortConfiguration = (state.value as ScreenState.Loaded).sortConfiguration,
            onDismissRequest = { shouldShowSortDialog = false },
            onApplySort = {
                shouldShowSortDialog = false
                onSortSelected(it)
            }
        )
    }

    ModalBottomSheetLayout(
        sheetContent = {
            Surface(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
                selectedGroup?.let { practiceGroup ->
                    GroupDetails(
                        group = practiceGroup,
                        onCharacterClick = navigateToCharacterInfo,
                        onStudyClick = { practiceConfiguration ->
                            navigateToPractice(practiceGroup, practiceConfiguration)
                        }
                    )
                }
            }
        },
        sheetState = sheetState
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(
                            onClick = navigateBack
                        ) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = navigateToEdit
                        ) {
                            Icon(painterResource(R.drawable.ic_outline_edit_24), null)
                        }
                        IconButton(
                            onClick = { shouldShowSortDialog = true },
                            enabled = state.value is ScreenState.Loaded
                        ) {
                            Icon(painterResource(R.drawable.ic_baseline_sort_24), null)
                        }
                    }
                )
            }
        ) { paddingValues ->

            AnimatedContent(
                targetState = state.value,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(),
                        initialContentExit = fadeOut()
                    )
                },
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
                            onGroupClick = {
                                selectedGroup = it
                                coroutineScope.launch { sheetState.show() }
                            }
                        )
                    }
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

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onGroupClick: (PracticeGroup) -> Unit
) {

    val rows = remember(screenState.groups) { screenState.groups.chunked(GroupsInRow) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {

        items(rows) { rowItems ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {

                Spacer(modifier = Modifier.width(10.dp))

                rowItems.forEach { group ->
                    GroupItem(
                        number = group.index,
                        text = group.items.joinToString("") { it.character },
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

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupItem(
    number: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {

        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .border(1.dp, Color.LightGray, CircleShape)
                    .padding(vertical = 4.dp, horizontal = 10.dp)
                    .wrapContentSize()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )

        }

    }

}

@Composable
fun GroupDetails(
    group: PracticeGroup,
    onCharacterClick: (String) -> Unit = {},
    onStudyClick: (PracticeConfiguration) -> Unit = {}
) {

    var shouldShowConfigDialog by remember { mutableStateOf(false) }
    var practiceConfiguration by rememberSaveable(group.index to group.lastDate) {
        mutableStateOf(
            PracticeConfiguration(
                isStudyMode = group.firstDate == null,
                shuffle = true
            )
        )
    }
    if (shouldShowConfigDialog) {
        key(group.index) {
            StudyDialog(
                defaultConfiguration = practiceConfiguration,
                onDismissRequest = { shouldShowConfigDialog = false },
                onApplyConfiguration = {
                    shouldShowConfigDialog = false
                    practiceConfiguration = it
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                onClick = { shouldShowConfigDialog = true }
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
                onClick = { onStudyClick(practiceConfiguration) }
            ) {
                Text(text = stringResource(R.string.practice_preview_practice_start))
            }

        }

    }

}


@Composable
private fun SortDialog(
    currentSortConfiguration: SortConfiguration,
    onDismissRequest: () -> Unit = {},
    onApplySort: (SortConfiguration) -> Unit
) {

    Dialog(
        onDismissRequest = onDismissRequest
    ) {

        Surface(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .verticalScroll(rememberScrollState())
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp)
            ) {

                Text(
                    text = stringResource(R.string.practice_preview_sort_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 8.dp
                    )
                )

                var selected by remember { mutableStateOf(currentSortConfiguration.sortOption) }
                var isDesc by remember { mutableStateOf(currentSortConfiguration.isDescending) }

                SortOption.values().forEach {

                    val rippleColor = LocalRippleTheme.current.defaultColor()
                    val rippleAlpha = LocalRippleTheme.current.rippleAlpha()

                    val rowColor by animateColorAsState(
                        targetValue = if (it == selected)
                            rippleColor.copy(alpha = rippleAlpha.pressedAlpha)
                        else rippleColor.copy(alpha = 0f)
                    )

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(rowColor)
                            .clickable {
                                if (selected == it) isDesc = !isDesc
                                else selected = it
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = stringResource(it.title),
                            modifier = Modifier.padding(start = 14.dp)
                        )

                        var showHint by remember { mutableStateOf(false) }

                        IconButton(onClick = { showHint = true }) {
                            Icon(painterResource(R.drawable.ic_baseline_help_outline_24), null)
                        }

                        CustomDropdownMenu(
                            expanded = showHint,
                            onDismissRequest = { showHint = false }
                        ) {
                            Text(
                                text = stringResource(it.hint),
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                                    .widthIn(max = 200.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (it == selected) {
                            val rotation by animateFloatAsState(
                                targetValue = if (isDesc) 90f else 270f
                            )
                            IconButton(onClick = { isDesc = !isDesc }) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.graphicsLayer(rotationZ = rotation)
                                )
                            }
                        }

                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.practice_preview_sort_cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onApplySort(
                                SortConfiguration(
                                    sortOption = selected,
                                    isDescending = isDesc
                                )
                            )
                        }
                    ) {
                        Text(text = stringResource(R.string.practice_preview_sort_apply))
                    }
                }

            }

        }
    }

}

@Composable
private fun StudyDialog(
    defaultConfiguration: PracticeConfiguration,
    onDismissRequest: () -> Unit = {},
    onApplyConfiguration: (PracticeConfiguration) -> Unit = {}
) {

    var isStudyMode by remember { mutableStateOf(defaultConfiguration.isStudyMode) }
    var shuffle by remember { mutableStateOf(defaultConfiguration.shuffle) }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {

        Surface(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .verticalScroll(rememberScrollState())
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp)
            ) {

                Text(
                    text = stringResource(R.string.practice_preview_config_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 8.dp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { isStudyMode = !isStudyMode }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.practice_preview_config_dialog_study_mode),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = isStudyMode, onCheckedChange = { isStudyMode = !isStudyMode })
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { shuffle = !shuffle }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.practice_preview_config_dialog_shuffle),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = shuffle, onCheckedChange = { shuffle = !shuffle })
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 20.dp)
                ) {
                    TextButton(
                        onClick = {
                            onApplyConfiguration(
                                PracticeConfiguration(
                                    isStudyMode = isStudyMode,
                                    shuffle = shuffle
                                )
                            )
                        }
                    ) {
                        Text(text = stringResource(R.string.practice_preview_config_dialog_apply))
                    }
                }

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
                    sortConfiguration = SortConfiguration.default,
                    groups = (1..9).map {
                        PracticeGroup(
                            index = it,
                            items = (1..6).map { PracticeGroupItem.random() },
                            firstDate = LocalDateTime.now(),
                            lastDate = LocalDateTime.now()
                        )
                    }
                )
            )
        }
        PracticePreviewScreenUI(
            title = "Test Practice",
            state = state
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupDetailsPreview() {
    AppTheme {
        GroupDetails(
            group = PracticeGroup(
                index = Random.nextInt(1, 100),
                items = (1..6).map { PracticeGroupItem.random() },
                firstDate = LocalDateTime.now(),
                lastDate = LocalDateTime.now()
            ),
            onStudyClick = {}
        )
    }
}

@Preview
@Composable
private fun SortDialogPreview() {
    AppTheme {
        SortDialog(
            currentSortConfiguration = SortConfiguration.default,
            onDismissRequest = {},
            onApplySort = {}
        )
    }
}

@Preview
@Composable
private fun StudyDialogPreview() {
    AppTheme {
        StudyDialog(
            defaultConfiguration = PracticeConfiguration(true, true)
        )
    }
}