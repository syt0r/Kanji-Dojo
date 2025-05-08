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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.stringResource
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.dialog_cancel
import ua.syt0r.kanji.presentation.common.AppDropdownMenu
import ua.syt0r.kanji.presentation.common.AppDropdownMenuItem
import ua.syt0r.kanji.presentation.common.AppListItem
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
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
                modifier = Modifier.padding(horizontal = Dimens.ContentPadding)
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
private val translationTextStyle
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
            .padding(vertical = Dimens.SpacingSmall)
    ) {

        when (val currentContentState = state.value) {
            TextAnalysisContentState.Empty -> {
                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                Text(
                    text = "Translation",
                    style = translationTextStyle,
                    modifier = Modifier.padding(vertical = Dimens.SpacingSmall)
                )
                Spacer(Modifier.weight(1f))
            }

            is TextAnalysisContentState.Loaded -> {

                when (val result = currentContentState.result) {

                    is TextAnalysisResult.Success -> {

                        TextAnalysisHeader(currentContentState)

                        AnalysisContent(
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
            .padding(top = 10.dp, bottom = Dimens.ContentPadding)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .height(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingTiny)
    ) {

        val inputState = state.value

        val isLoading: Boolean
        val input: String
        val canEdit: Boolean
        val isInputValid: Boolean
        val canSubmit: Boolean

        when (inputState) {
            is TextAnalysisInputState.Loading -> {
                isLoading = true
                input = inputState.input
                canEdit = false
                isInputValid = true
                canSubmit = false
            }

            is TextAnalysisInputState.Typing -> {
                isLoading = false
                input = inputState.input.value
                canEdit = true
                isInputValid = inputState.isInputValid.value
                canSubmit = inputState.isInputValid.value
            }
        }

        val inputTextStyle = MaterialTheme.typography.bodyMedium
            .copy(color = MaterialTheme.colorScheme.onSurface)

        BasicTextField(
            value = input,
            readOnly = !canEdit,
            onValueChange = {
                inputState as TextAnalysisInputState.Typing
                inputState.input.value = it
            },
            maxLines = 4,
            decorationBox = { content ->
                val showPlaceholder = input.isEmpty()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.ContentPadding)
                        .padding(top = Dimens.ContentPadding)
                        .let { if (showPlaceholder) it.offset(-Dimens.SpacingTiny) else it }
                ) {
                    if (input.isEmpty()) {
                        Text(
                            text = "Enter text here",
                            style = inputTextStyle.copy(
                                color = inputTextStyle.color.copy(alpha = 0.38f)
                            ),
                            modifier = Modifier.padding(horizontal = Dimens.SpacingTiny)
                        )
                    }
                    content()
                }
            },
            textStyle = inputTextStyle,
            cursorBrush = SolidColor(inputTextStyle.color),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.SpacingMid),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = when {
                                isInputValid -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> MaterialTheme.colorScheme.error
                            }
                        )
                    ) { append(input.length.toString()) }
                    append("/${TextAnalysisContract.INPUT_LIMIT}")
                },
                style = MaterialTheme.typography.labelSmall.copyCentered(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Dimens.SpacingBig)
            )

            Spacer(Modifier.weight(1f))

            var showConfigurationDialog by remember { mutableStateOf(false) }
            if (showConfigurationDialog) {
                TextAnalysisConfigurationDialog(
                    onDismissRequest = { showConfigurationDialog = false }
                )
            }

            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { showConfigurationDialog = true }
                    .wrapContentSize()
                    .size(Dimens.IconSmall)
            )

            Crossfade(
                targetState = isLoading,
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(canSubmit) {
                        inputState as TextAnalysisInputState.Typing
                        inputState.submit()
                    }
            ) { isLoading ->
                val modifier = Modifier.fillMaxSize().wrapContentSize()
                when {
                    isLoading -> CircularProgressIndicator(modifier.size(Dimens.Icon))
                    else -> Icon(
                        imageVector = Icons.AutoMirrored.Default.NavigateNext,
                        contentDescription = null,
                        modifier = modifier,
                        tint = when {
                            isInputValid -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        }
                    )
                }

            }

        }

    }
}

@Composable
private fun TextAnalysisConfigurationDialog(onDismissRequest: () -> Unit) {

    val badge: @Composable RowScope.(String) -> Unit = {
        Row {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(horizontal = Dimens.ContentPaddingSmall, vertical = Dimens.SpacingMid)
                    .alignByBaseline()
            )
        }

    }

    MultiplatformDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Configuration") },
        content = {
            Text("Analysis Provider")
            FlowRow { badge("Ichiran") }
            Text("Translation Provider")
            FlowRow { badge("Gemini AI") }
        },
        buttons = {
            TextButton(onDismissRequest) {
                Text(stringResource(Res.string.dialog_cancel))
            }
        }
    )

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TextAnalysisHeader(contentState: TextAnalysisContentState.Loaded) {

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

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.End)
                            .width(IntrinsicSize.Max)
                            .height(IntrinsicSize.Max)
                    ) {
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

            is TextAnalysisContentMode.SaveLetters -> {

                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    val count = currentMode.selected.value.size

                    Text(
                        text = "Selected letters: $count",
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

                    TextButton(
                        onClick = currentMode.selectAllKanji,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("Select All Kanji")
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.End)
                            .width(IntrinsicSize.Max)
                            .height(IntrinsicSize.Max)
                    ) {
                        TextButton(
                            onClick = currentMode.switchToBrowseMode,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("Cancel")
                        }

                        TextButton(
                            onClick = { },
                            modifier = Modifier.align(Alignment.CenterVertically),
                            enabled = count > 0
                        ) {
                            Text("Add To Deck")
                        }
                    }

                }

            }
        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.AnalysisContent(
    displayMode: TextAnalysisContentMode,
    nodeList: List<TextAnalysisNode>,
    translation: String
) {

    when (displayMode) {
        is TextAnalysisContentMode.WordsDisplay -> {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.SpacingMid)
            ) {
                nodeList.forEach {
                    WordNode(
                        node = it,
                        displayMode = displayMode
                    )
                }
            }
        }

        is TextAnalysisContentMode.SaveLetters -> {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.SpacingMid)
            ) {
                displayMode.letters.forEach {
                    TODO("implement letters saving")
                }
            }
        }
    }

    Spacer(modifier = Modifier.weight(1f))

    HorizontalDivider()

    SelectionContainer(
        modifier = Modifier.padding(vertical = Dimens.SpacingMid)
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
private fun RowScope.WordNode(
    node: TextAnalysisNode,
    displayMode: TextAnalysisContentMode.WordsDisplay
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

        is TextAnalysisNode.Error -> Column(
            modifier = Modifier
                .alignByBaseline()
                .width(IntrinsicSize.Max)
        ) {

            Text(text = node.text ?: "Error")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.SpacingTiny)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
            )

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
    displayMode: TextAnalysisContentMode.WordsDisplay
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
            .padding(Dimens.SpacingMid, Dimens.SpacingSmall)
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
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
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
                        .height(Dimens.SpacingTiny)
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
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
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
                                        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)
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
