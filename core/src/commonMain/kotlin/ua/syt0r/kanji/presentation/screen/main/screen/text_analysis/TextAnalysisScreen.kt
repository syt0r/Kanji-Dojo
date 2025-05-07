package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Deselect
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import ua.syt0r.kanji.presentation.common.AppDropdownMenu
import ua.syt0r.kanji.presentation.common.AppDropdownMenuItem
import ua.syt0r.kanji.presentation.common.AppListItem
import ua.syt0r.kanji.presentation.common.PaginationLoadLaunchedEffect
import ua.syt0r.kanji.presentation.common.collectAsState
import ua.syt0r.kanji.presentation.common.copyCentered
import ua.syt0r.kanji.presentation.common.theme.Dimens
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.FancyLoading
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.VerticalScrollbar
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.text_analysis.TextAnalysisContract.ScreenState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAnalysisScreen(
    navigationState: MainNavigationState,
    viewModel: TextAnalysisViewModel = getMultiplatformViewModel()
) {

    val screenState = viewModel.state.collectAsState()

    var showHistory = rememberSaveable { mutableStateOf(false) }

    ScreenLayout(
        state = screenState,
        showHistory = showHistory,
        navigateBack = { navigationState.navigateBack() },
        analysisContent = { screenState ->

            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {

                AnalysisResultSection(
                    state = screenState.contentState.collectAsState(),
                    modifier = Modifier.weight(1f)
                )

                HorizontalDivider()

                InputSection(
                    state = screenState.inputState.collectAsState()
                )

            }

        },
        historyContent = { screenState ->
            val historyPaginateable = screenState.history.collectAsState()
            val historyState = historyPaginateable.value.collectAsState()

            val listState = rememberLazyListState()
            PaginationLoadLaunchedEffect(listState) { historyPaginateable.value.loadMore() }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {

                items(historyState.list) {
                    AppListItem(
                        onClick = {
                            screenState.setContent(it)
                            showHistory.value = false
                        },
                        headlineContent = {
                            Text(
                                text = it.text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }

                item { Spacer(Modifier.height(Dimens.ContentPadding)) }

            }
        }
    )


}

@get:Composable
val translationTextStyle
    get() = MaterialTheme.typography.titleLarge.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

@Composable
private fun AnalysisResultSection(
    state: State<TextAnalysisContentState>,
    modifier: Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp)
    ) {

        when (val currentContentState = state.value) {
            TextAnalysisContentState.Empty -> {
                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                Text(
                    text = "Translation",
                    style = translationTextStyle,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(Modifier.weight(1f))
            }

            is TextAnalysisContentState.Loaded -> {

                when (val result = currentContentState.result) {

                    is TextAnalysisResult.Success -> {

                        TextAnalysisModeRow(currentContentState)

                        TextLayout(
                            displayMode = currentContentState.contentMode.value,
                            nodeList = result.nodeList,
                            translation = result.translation
                        )

                    }

                    is TextAnalysisResult.Error -> {
                        Text(
                            text = result.message,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .wrapContentSize()
                                .width(400.dp)
                        )
                    }
                }
            }

        }

    }

}

@Composable
private fun InputSection(
    state: State<TextAnalysisInputState>
) {

    Column(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .padding(top = 10.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        val inputState = state.value

        val input: String
        val canSubmit: Boolean

        when (inputState) {
            is TextAnalysisInputState.Loading -> {
                input = inputState.input
                canSubmit = false
            }

            is TextAnalysisInputState.Typing -> {
                input = inputState.input.value
                canSubmit = inputState.isInputValid.value
            }
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            TextField(
                value = input,
                readOnly = !canSubmit,
                onValueChange = {
                    inputState as TextAnalysisInputState.Typing
                    inputState.input.value = it
                },
                maxLines = 4,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    errorCursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                    errorLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
                )
            )

            Crossfade(
                targetState = canSubmit,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 8.dp)
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(canSubmit) {
                        inputState as TextAnalysisInputState.Typing
                        inputState.submit()
                    }
            ) { canSubmit ->
                val modifier = Modifier.fillMaxSize().wrapContentSize()
                when {
                    canSubmit -> Icon(
                        imageVector = Icons.AutoMirrored.Default.NavigateNext,
                        contentDescription = null,
                        modifier = modifier
                    )

                    else -> CircularProgressIndicator(modifier)
                }

            }

        }

        Text(
            text = "${input.length}/${TextAnalysisContract.INPUT_LIMIT}",
            style = MaterialTheme.typography.labelSmall.copyCentered(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TextAnalysisModeRow(contentState: TextAnalysisContentState.Loaded) {

    Crossfade(
        targetState = contentState.contentMode.value,
        modifier = Modifier.fillMaxWidth()
    ) { currentMode ->

        when (currentMode) {
            is TextAnalysisContentMode.Browse -> {

                var showPopup by remember { mutableStateOf(false) }

                IconButton(
                    onClick = { showPopup = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End)
                ) {

                    var furigana by currentMode.furigana
                    var highlight by currentMode.highlight

                    Icon(Icons.Outlined.MoreHoriz, null)

                    AppDropdownMenu(
                        expanded = showPopup,
                        onDismissRequest = { showPopup = false },
                        modifier = Modifier.width(IntrinsicSize.Max)
                    ) {
                        AppDropdownMenuItem(
                            onClick = { furigana = !furigana }
                        ) {
                            Icon(Icons.Outlined.Translate, null)
                            Text("Furigana", Modifier.weight(1f))
                            if (furigana) Icon(Icons.Outlined.Check, null)
                            else Icon(Icons.Outlined.Close, null)
                        }
                        AppDropdownMenuItem(
                            onClick = { highlight = !highlight }
                        ) {
                            Icon(Icons.Outlined.BorderColor, null)
                            Text("Highlight", Modifier.weight(1f))
                            if (highlight) Icon(Icons.Outlined.Check, null)
                            else Icon(Icons.Outlined.Close, null)
                        }
                        AppDropdownMenuItem(currentMode.switchToSaveWordsMode) {
                            Icon(Icons.Outlined.Bookmarks, null)
                            Text("Save Words")
                        }
                    }

                }

            }

            is TextAnalysisContentMode.SaveWords -> {

                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    val wordsCount = currentMode.selected.value.size

                    Text(
                        text = "Selected words: $wordsCount",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 8.dp),
                        style = MaterialTheme.typography.labelMedium.copyCentered()
                    )

                    IconButton(
                        onClick = currentMode.selectNone,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    ) { Icon(Icons.Outlined.Deselect, null) }

                    IconButton(
                        onClick = currentMode.selectAll,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) { Icon(Icons.Outlined.SelectAll, null) }

                    Spacer(Modifier.weight(1f))

                    TextButton(
                        onClick = currentMode.switchToBrowseMode,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.CenterVertically),
                        enabled = wordsCount > 0
                    ) {
                        Text("Add To Deck")
                    }

                }

            }
        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.TextLayout(
    displayMode: TextAnalysisContentMode,
    nodeList: List<TextAnalysisNode>,
    translation: String
) {

    FlowRow(
        modifier = Modifier.height(IntrinsicSize.Max),
        overflow = FlowRowOverflow.Visible
    ) {
        nodeList.forEach {
            TextAnalysisNode(
                node = it,
                displayMode = displayMode
            )
        }
    }

    Spacer(modifier = Modifier.weight(1f))

    HorizontalDivider()

    SelectionContainer(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = translation,
            style = translationTextStyle
        )
    }

    Box(modifier = Modifier.weight(1f))

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RowScope.TextAnalysisNode(
    node: TextAnalysisNode,
    displayMode: TextAnalysisContentMode
) {
    when (node) {
        is TextAnalysisNode.Word -> WordNode(node, displayMode)

        is TextAnalysisNode.Text -> {
            Text(
                text = node.value,
                modifier = Modifier.alignByBaseline()
            )
        }

        is TextAnalysisNode.AlternativeWords -> {
            WordNode(node.words.first(), displayMode)
        }
    }
}

@Composable
fun TextAnalysisNode.PartOfSpeech.toHighlightColor(
    noHighlightColor: Color = Color.Unspecified
): Color {
    return when (this) {
        TextAnalysisNode.PartOfSpeech.Noun -> MaterialTheme.extraColorScheme.new
        TextAnalysisNode.PartOfSpeech.Verb -> MaterialTheme.extraColorScheme.success
        TextAnalysisNode.PartOfSpeech.Adj -> MaterialTheme.extraColorScheme.due
        else -> noHighlightColor
    }
}

@Composable
private fun RowScope.WordNode(
    node: TextAnalysisNode.Word,
    displayMode: TextAnalysisContentMode
) {

    var showPopup = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable {
                when (displayMode) {
                    is TextAnalysisContentMode.Browse -> {
                        showPopup.value = true
                    }

                    is TextAnalysisContentMode.SaveWords -> {
                        displayMode.toggleSelection(node)
                    }
                }
            }
            .padding(8.dp, 4.dp)
            .width(IntrinsicSize.Max)
            .alignByBaseline()
    ) {

        Box(Modifier.align(Alignment.BottomStart)) {
            WordDetailsPopup(
                showPopup = showPopup,
                node = node
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            when {
                displayMode is TextAnalysisContentMode.Browse &&
                        displayMode.furigana.value.not() -> Text(node.text)

                else -> FuriganaText(node.furigana)
            }

            val highlightColor: Color?

            when (displayMode) {
                is TextAnalysisContentMode.Browse -> {
                    highlightColor = if (!displayMode.highlight.value) null
                    else node.highlightPartOfSpeech
                        ?.toHighlightColor(MaterialTheme.colorScheme.surface)
                }

                is TextAnalysisContentMode.SaveWords -> {
                    highlightColor = if (displayMode.selected.value.contains(node))
                        MaterialTheme.extraColorScheme.success
                    else MaterialTheme.colorScheme.surfaceVariant
                }
            }

            if (highlightColor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(highlightColor, CircleShape)
                )
            }
        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordDetailsPopup(showPopup: MutableState<Boolean>, node: TextAnalysisNode.Word) {
    if (showPopup.value.not()) return

    val scrollState = rememberScrollState()

    Popup(
        onDismissRequest = { showPopup.value = false },
        properties = PopupProperties()
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .shadow(Dimens.SpacingMid, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceDim)
        ) {

            Column(
                modifier = Modifier
                    .sizeIn(minWidth = 160.dp, maxWidth = 300.dp, maxHeight = 300.dp)
                    .verticalScroll(scrollState)
                    .padding(vertical = Dimens.ContentPaddingSmall)
                    .padding(start = Dimens.ContentPaddingSmall),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)
                ) {
                    Text(
                        text = node.reading,
                        style = MaterialTheme.typography.bodyLarge.copyCentered(),
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clip(MaterialTheme.shapes.small)
                            .clickable { }
                            .padding(Dimens.SpacingTiny)
                    )
                }

                if (node.combinedPartOfSpeechList.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)) {
                        node.combinedPartOfSpeechList.forEach {
                            val highlightColor = it
                                .toHighlightColor(MaterialTheme.colorScheme.surfaceVariant)

                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Dimens.SpacingTiny))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .drawWithContent {
                                        drawContent()
                                        val highlightHeightPx = Dimens.SpacingTiny.toPx()
                                        drawRect(
                                            color = highlightColor,
                                            topLeft = Offset(0f, size.height - highlightHeightPx),
                                            size = size.copy(height = highlightHeightPx)
                                        )
                                    }
                                    .padding(vertical = Dimens.SpacingTiny)
                                    .padding(
                                        horizontal = Dimens.SpacingMid,
                                        vertical = Dimens.SpacingSmall
                                    )
                                    .alignByBaseline()
                            )
                        }
                    }
                }

                node.glossary.forEachIndexed { i, it ->
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)
                        ) {
                            val style = MaterialTheme.typography.bodySmall
                            Text(i.plus(1).toString(), style = style)
                            Text(it.definition, Modifier.weight(1f), style = style)
                        }
                    }
                }
            }

            VerticalScrollbar(
                scrollState = scrollState,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = Dimens.SpacingMid)
                    .padding(end = Dimens.SpacingTiny)
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenLayout(
    state: State<ScreenState>,
    showHistory: MutableState<Boolean>,
    navigateBack: () -> Unit,
    analysisContent: @Composable (ScreenState.Loaded) -> Unit,
    historyContent: @Composable (ScreenState.Loaded) -> Unit,
) {

    BoxWithConstraints {

        val splitView = maxWidth > 800.dp

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = navigateBack
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    title = {
                        val title = when {
                            showHistory.value && !splitView -> {
                                "History"
                            }

                            else -> {
                                "Text Analysis"
                            }
                        }
                        Text(text = title)
                    },
                    actions = {
                        if (!splitView) {
                            IconButton(
                                onClick = { showHistory.value = !showHistory.value }
                            ) {
                                val switchActionIcon: ImageVector = when {
                                    showHistory.value -> Icons.AutoMirrored.Outlined.ManageSearch
                                    else -> Icons.Outlined.History
                                }
                                Icon(switchActionIcon, null)
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->

            Crossfade(
                targetState = state.value to showHistory.value,
                modifier = Modifier.padding(paddingValues)
            ) { (screenState, showHistory) ->
                when (screenState) {
                    ScreenState.Loading -> {
                        FancyLoading(Modifier.fillMaxSize().wrapContentSize())
                    }

                    is ScreenState.Loaded -> {
                        when {
                            splitView -> {
                                Row {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "History",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        historyContent(screenState)
                                    }
                                    VerticalDivider()
                                    Box(Modifier.weight(2f)) { analysisContent(screenState) }
                                }
                            }

                            showHistory -> historyContent(screenState)
                            else -> analysisContent(screenState)
                        }
                    }
                }
            }

        }

    }

}
