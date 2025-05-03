package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableColumn
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.launchOnInvoke
import ua.syt0r.kanji.core.srs.LetterPracticeType
import ua.syt0r.kanji.core.srs.VocabPracticeType
import ua.syt0r.kanji.dialog_apply
import ua.syt0r.kanji.dialog_cancel
import ua.syt0r.kanji.general_dashboard_downloads
import ua.syt0r.kanji.general_dashboard_header_reviews
import ua.syt0r.kanji.general_dashboard_header_streak_current
import ua.syt0r.kanji.general_dashboard_header_streak_longest
import ua.syt0r.kanji.general_dashboard_social
import ua.syt0r.kanji.general_dashboard_study_target_daily_limit
import ua.syt0r.kanji.general_dashboard_study_target_edit
import ua.syt0r.kanji.general_dashboard_study_target_empty
import ua.syt0r.kanji.general_dashboard_study_target_no_decks
import ua.syt0r.kanji.general_dashboard_study_target_nothing_left
import ua.syt0r.kanji.general_dashboard_study_target_title
import ua.syt0r.kanji.general_dashboard_text_analysis
import ua.syt0r.kanji.general_dashboard_tutorial
import ua.syt0r.kanji.presentation.common.AppDropdownMenu
import ua.syt0r.kanji.presentation.common.AppDropdownMenuItem
import ua.syt0r.kanji.presentation.common.AppListItem
import ua.syt0r.kanji.presentation.common.AppListItemDefaults
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.ScreenLetterPracticeType
import ua.syt0r.kanji.presentation.common.ScreenVocabPracticeType
import ua.syt0r.kanji.presentation.common.copyCentered
import ua.syt0r.kanji.presentation.common.theme.Dimens
import ua.syt0r.kanji.presentation.common.theme.snapSizeTransform
import ua.syt0r.kanji.presentation.common.ui.FancyLoading
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.GeneralDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.ui.TutorialDialog
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeScreenConfiguration
import ua.syt0r.kanji.srs_status_due
import ua.syt0r.kanji.srs_status_new
import kotlin.math.roundToInt

@Composable
fun GeneralDashboardScreenUI(
    state: State<ScreenState>,
    navigateToDailyLimitConfiguration: () -> Unit,
    navigateToCreateLetterDeck: () -> Unit,
    navigateToCreateVocabDeck: () -> Unit,
    navigateToLetterPractice: (MainDestination.LetterPractice) -> Unit,
    navigateToVocabPractice: (MainDestination.VocabPractice) -> Unit,
    downloadsClick: () -> Unit,
    socialClick: (SocialButton) -> Unit,
    textAnalysisClick: () -> Unit
) {

    var showTutorialDialog by remember { mutableStateOf(false) }
    if (showTutorialDialog) {
        TutorialDialog { showTutorialDialog = false }
    }

    ScreenLayout(state) { screenState, snackbarHostState ->

        val coroutineScope = rememberCoroutineScope()
        var showStudyTargetsEditDialog by rememberSaveable { mutableStateOf(false) }
        if (showStudyTargetsEditDialog) {
            StudyTargetsEditDialog(
                onDismissRequest = { showStudyTargetsEditDialog = false },
                state = screenState
            )
        }


        Header(screenState)

        ScreenDivider()

        StudyTargets(
            state = screenState,
            showEditDialog = { showStudyTargetsEditDialog = true },
            navigateToDailyLimitConfiguration = navigateToDailyLimitConfiguration,
            navigateToCreateLetterDeck = navigateToCreateLetterDeck,
            navigateToCreateVocabDeck = navigateToCreateVocabDeck,
            navigateToLetterPractice = navigateToLetterPractice,
            navigateToVocabPractice = navigateToVocabPractice,
            notifyNothingLeftToStudy = coroutineScope.launchOnInvoke {
                val message = getString(Res.string.general_dashboard_study_target_nothing_left)
                snackbarHostState.showSnackbar(message, withDismissAction = true)
            }
        )

        ScreenDivider()

        AppListItem(
            onClick = textAnalysisClick,
            headlineContent = { Text(stringResource(Res.string.general_dashboard_text_analysis)) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
        )

        SocialButton(
            selected = socialClick
        )

        AppListItem(
            onClick = downloadsClick,
            headlineContent = { Text(stringResource(Res.string.general_dashboard_downloads)) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
        )

        AppListItem(
            onClick = { showTutorialDialog = true },
            headlineContent = { Text(stringResource(Res.string.general_dashboard_tutorial)) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
        )

    }

}

@Composable
private fun ScreenDivider() {
    HorizontalDivider(Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
}

@Composable
private fun StudyTargetsEditDialog(
    onDismissRequest: () -> Unit,
    state: ScreenState.Loaded
) {

    var states by remember {
        mutableStateOf(state.studyTargets.value)
    }

    val toggleEnabledAtIndex = { index: Int ->
        states = states.toMutableList().apply {
            val itemState = get(index).run { copy(enabled = !enabled) }
            set(index, itemState)
        }
    }

    MultiplatformDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.general_dashboard_study_target_title)) },
        paddedContent = false,
        content = {
            ReorderableColumn(
                list = states.toList(),
                onSettle = { fromIndex, toIndex ->
                    states = states.toList()
                        .toMutableList()
                        .apply { add(toIndex, removeAt(fromIndex)) }
                }
            ) { index, item, _ ->
                val studyTarget = item.studyTarget
                key(studyTarget) {
                    AppListItem(
                        onClick = { toggleEnabledAtIndex(index) },
                        leadingContent = {
                            Icon(Icons.Outlined.DragIndicator, null, Modifier.draggableHandle())
                        },
                        overlineContent = { Text(stringResource(studyTarget.categoryTitle)) },
                        headlineContent = { Text(stringResource(studyTarget.typeTitleRes)) },
                        trailingContent = {
                            Switch(
                                checked = item.enabled,
                                onCheckedChange = { toggleEnabledAtIndex(index) }
                            )
                        }
                    )
                }
            }
        },
        buttons = {
            TextButton(onDismissRequest) {
                Text(stringResource(Res.string.dialog_cancel))
            }
            TextButton(
                onClick = {
                    state.studyTargets.value = states
                    onDismissRequest()
                }
            ) {
                Text(stringResource(Res.string.dialog_apply))
            }
        }
    )
}

@Composable
private fun SocialButton(selected: (SocialButton) -> Unit) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }

    AppListItem(
        onClick = { showDropdown = true },
        headlineContent = { Text(stringResource(Res.string.general_dashboard_social)) },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
            AppDropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                SocialButton.entries.forEach {
                    AppDropdownMenuItem(
                        onClick = { selected(it) },
                        content = {
                            Icon(painterResource(it.icon), null)
                            Text(stringResource(it.title))
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun StudyTargets(
    state: ScreenState.Loaded,
    showEditDialog: () -> Unit,
    navigateToDailyLimitConfiguration: () -> Unit,
    navigateToCreateLetterDeck: () -> Unit,
    navigateToCreateVocabDeck: () -> Unit,
    navigateToLetterPractice: (MainDestination.LetterPractice) -> Unit,
    navigateToVocabPractice: (MainDestination.VocabPractice) -> Unit,
    notifyNothingLeftToStudy: () -> Unit
) {

    Column {

        Row(
            modifier = Modifier.padding(start = 24.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(Res.string.general_dashboard_study_target_title),
                style = MaterialTheme.typography.titleLarge.copyCentered()
            )
            var showPopup by remember { mutableStateOf(false) }

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = { showPopup = true }
            ) {

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )

                AppDropdownMenu(
                    expanded = showPopup,
                    onDismissRequest = { showPopup = false }
                ) {
                    AppDropdownMenuItem(
                        onClick = {
                            showEditDialog()
                            showPopup = false
                        }
                    ) {
                        Icon(Icons.Outlined.Edit, null)
                        Text(stringResource(Res.string.general_dashboard_study_target_edit))
                    }
                    AppDropdownMenuItem(
                        onClick = {
                            navigateToDailyLimitConfiguration()
                            showPopup = false
                        }
                    ) {
                        Icon(Icons.Outlined.Settings, null)
                        Text(stringResource(Res.string.general_dashboard_study_target_daily_limit))
                    }
                }

            }

        }

        val displayList = state.studyTargets.value.filter { it.enabled }

        if (displayList.isEmpty()) {
            AppListItem(
                onClick = showEditDialog,
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.general_dashboard_study_target_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            displayList.forEach {
                StudyTargetItem(
                    studyTargetState = it,
                    createDeck = {
                        when (it.studyTarget.practiceType) {
                            is LetterPracticeType -> navigateToCreateLetterDeck()
                            is VocabPracticeType -> navigateToCreateVocabDeck()
                        }
                    },
                    startPractice = { map ->
                        if (map.isEmpty()) {
                            notifyNothingLeftToStudy()
                            return@StudyTargetItem
                        }
                        when (val practiceType = it.studyTarget.practiceType) {
                            is LetterPracticeType -> {
                                val configuration = LetterPracticeScreenConfiguration(
                                    characterToDeckIdMap = map as Map<String, Long>,
                                    practiceType = ScreenLetterPracticeType.from(practiceType)
                                )
                                val destination = MainDestination.LetterPractice(configuration)
                                navigateToLetterPractice(destination)
                            }

                            is VocabPracticeType -> {
                                val configuration = VocabPracticeScreenConfiguration(
                                    wordIdToDeckIdMap = map as Map<Long, Long>,
                                    practiceType = ScreenVocabPracticeType.from(practiceType)
                                )
                                val destination = MainDestination.VocabPractice(configuration)
                                navigateToVocabPractice(destination)
                            }
                        }
                    }
                )
            }
        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyTargetItem(
    studyTargetState: StudyTargetState,
    createDeck: () -> Unit,
    startPractice: (Map<out Any, Long>) -> Unit
) {

    val studyTarget = studyTargetState.studyTarget
    val studyProgress = studyTargetState.progress

    AppListItem(
        onClick = {
            when (studyProgress) {
                StudyTargetProgress.NoDecks -> createDeck()
                is StudyTargetProgress.WithDecks -> {
                    startPractice(studyProgress.options.combined)
                }
            }
        },
        headlineContent = {
            Text(
                stringResource(studyTarget.categoryTitle) + "・" + stringResource(studyTarget.typeTitleRes)
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null
            )
        },
        supportingContent = {
            if (studyProgress is StudyTargetProgress.NoDecks) {
                Text(stringResource(Res.string.general_dashboard_study_target_no_decks))
                return@AppListItem
            }

            studyProgress as StudyTargetProgress.WithDecks

            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingTiny)
            ) {

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(-Dimens.SpacingMid)
                ) {

                    ClickableStudyRow(
                        imageVector = Icons.Outlined.School,
                        title = stringResource(Res.string.srs_status_new),
                        count = studyProgress.options.newToDeckIdMap.size,
                        onClick = { startPractice(studyProgress.options.newToDeckIdMap) }
                    )

                    ClickableStudyRow(
                        imageVector = Icons.Outlined.Schedule,
                        title = stringResource(Res.string.srs_status_due),
                        count = studyProgress.options.dueToDeckIdMap.size,
                        onClick = { startPractice(studyProgress.options.dueToDeckIdMap) }
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)
                ) {

                    LinearProgressIndicator(
                        progress = studyProgress.totalProgress,
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = studyProgress.totalProgress.times(100).roundToInt().toString() + "%",
                        style = LocalTextStyle.current.copyCentered()
                    )

                }
            }
        }
    )
}

@Composable
private fun ClickableStudyRow(
    imageVector: ImageVector,
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.SpacingMid, vertical = Dimens.SpacingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
    ) {
        val iconSize = 18.dp
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )

        val textStyle = LocalTextStyle.current.copyCentered()

        Text(
            text = title,
            style = textStyle
        )

        if (count == 0) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconSize)
            )
        } else {
            Text(
                text = count.toString(),
                style = textStyle
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Header(state: ScreenState.Loaded) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 4.dp
            )
            .padding(AppListItemDefaults.ExtraPaddings)
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        fun Int.numberOrDash(): String = if (this == 0) "-" else toString()
        HeaderStatItem(
            title = stringResource(Res.string.general_dashboard_header_streak_current),
            text = state.stats.currentStreak.numberOrDash(),
            modifier = Modifier.weight(1f)
        )
        HeaderStatItem(
            title = stringResource(Res.string.general_dashboard_header_streak_longest),
            text = state.stats.longestStreak.numberOrDash(),
            modifier = Modifier.weight(1f)
        )
        HeaderStatItem(
            title = stringResource(Res.string.general_dashboard_header_reviews),
            text = state.stats.reviewsToday.numberOrDash(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HeaderStatItem(title: String, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }

    }
}


@Composable
private fun ScreenLayout(
    state: State<ScreenState>,
    content: @Composable ColumnScope.(ScreenState.Loaded, SnackbarHostState) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Box {
        AnimatedContent(
            targetState = state.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() using snapSizeTransform() }
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> {
                    FancyLoading(Modifier.fillMaxSize().wrapContentSize())
                }

                is ScreenState.Loaded -> Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .wrapContentWidth()
                        .width(400.dp)
                ) {

                    if (LocalOrientation.current == Orientation.Landscape) {
                        Spacer(Modifier.height(20.dp))
                    }

                    content(screenState, snackbarHostState)

                    Spacer(Modifier.height(20.dp))

                }

            }

        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionContentColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        )

    }

}
