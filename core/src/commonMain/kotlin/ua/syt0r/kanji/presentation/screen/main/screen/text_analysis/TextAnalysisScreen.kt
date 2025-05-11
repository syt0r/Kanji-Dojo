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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.outlined.ErrorOutline
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
import ua.syt0r.kanji.core.app_data.data.formattedVocabStringReading
import ua.syt0r.kanji.dialog_cancel
import ua.syt0r.kanji.presentation.common.AppDropdownMenu
import ua.syt0r.kanji.presentation.common.AppDropdownMenuItem
import ua.syt0r.kanji.presentation.common.AppListItem
import ua.syt0r.kanji.presentation.common.AppTextField
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.PaginationLoadLaunchedEffect
import ua.syt0r.kanji.presentation.common.clickable
import ua.syt0r.kanji.presentation.common.collectAsState
import ua.syt0r.kanji.presentation.common.copyCentered
import ua.syt0r.kanji.presentation.common.theme.Dimens
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.FancyLoading
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.VerticalScrollbar
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.text_analysis.TextAnalysisContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.vocab_card.SuggestedVocabCardData
import ua.syt0r.kanji.presentation.screen.main.screen.vocab_card.VocabCardScreenMode
import ua.syt0r.kanji.text_analysis_action_furigana
import ua.syt0r.kanji.text_analysis_action_highlight
import ua.syt0r.kanji.text_analysis_action_save_words
import ua.syt0r.kanji.text_analysis_configuration_analysis_provider
import ua.syt0r.kanji.text_analysis_configuration_title
import ua.syt0r.kanji.text_analysis_configuration_translation_provider
import ua.syt0r.kanji.text_analysis_history_empty
import ua.syt0r.kanji.text_analysis_history_title
import ua.syt0r.kanji.text_analysis_ichiran_description
import ua.syt0r.kanji.text_analysis_input_placeholder
import ua.syt0r.kanji.text_analysis_offer_subtitle
import ua.syt0r.kanji.text_analysis_offer_title
import ua.syt0r.kanji.text_analysis_save_letters_apply
import ua.syt0r.kanji.text_analysis_save_letters_cancel
import ua.syt0r.kanji.text_analysis_save_letters_counter
import ua.syt0r.kanji.text_analysis_save_letters_select_kanji
import ua.syt0r.kanji.text_analysis_save_words_apply
import ua.syt0r.kanji.text_analysis_save_words_cancel
import ua.syt0r.kanji.text_analysis_save_words_counter
import ua.syt0r.kanji.text_analysis_title
import ua.syt0r.kanji.text_analysis_translation_placeholder
import ua.syt0r.kanji.text_analysis_word_parse_error


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
                    saveWord = { wordNode ->
                        val reading = wordNode.dictionaryReading ?: wordNode.reading
                        val destination = MainDestination.VocabCard(
                            screenMode = VocabCardScreenMode.Save,
                            cardData = SuggestedVocabCardData(
                                kanjiReading = reading.kanjiReading,
                                kanaReading = reading.kanaReading,
                                suggestedMeanings = wordNode.glossary.map { it.definition },
                                jmDictId = wordNode.sequence,
                                cardId = null
                            )
                        )
                        navigationState.navigate(destination)
                    },
                    modifier = Modifier.weight(1f)
                )

                HorizontalDivider()

                InputSection(
                    state = screenState.inputState.collectAsState(),
                    navigateToAccount = { navigationState.navigate(MainDestination.Account()) }
                )

            }

        },
        historyContent = { screenState ->
            val historyPaginateable = screenState.history.collectAsState()

            if (historyPaginateable.value.total == 0) {
                Text(
                    text = stringResource(Res.string.text_analysis_history_empty),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
                return@ScreenLayout
            }

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
    saveWord: (TextAnalysisNode.Word) -> Unit,
    modifier: Modifier,
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
                    text = stringResource(Res.string.text_analysis_translation_placeholder),
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
                            translation = result.translation,
                            saveWord = saveWord
                        )

                    }

                    is TextAnalysisResult.Error -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .wrapContentSize()
                                .widthIn(max = Dimens.ScreenWidth)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.alignBy { it.measuredHeight * 4 / 5 }
                            )
                            SelectionContainer(
                                modifier = Modifier.alignByBaseline()
                            ) {
                                Text(text = result.message)
                            }
                        }

                    }
                }
            }

        }

    }

}

@Composable
private fun InputSection(
    state: State<TextAnalysisInputState>,
    navigateToAccount: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(top = Dimens.SpacingBig, bottom = Dimens.ContentPadding)
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

            is TextAnalysisInputState.NotEligible -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.ContentPaddingSmall)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.text_analysis_offer_title),
                            style = MaterialTheme.typography.titleMedium,
                            lineHeight = MaterialTheme.typography.titleMedium.fontSize
                        )
                        Text(
                            text = stringResource(Res.string.text_analysis_offer_subtitle),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Default.NavigateNext,
                        contentDescription = null,
                        modifier = Modifier
                            .size(Dimens.IconButton)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable(navigateToAccount)
                            .wrapContentSize()
                            .size(Dimens.Icon)
                    )
                }

                return@Column
            }
        }

        AppTextField(
            value = input,
            readOnly = !canEdit,
            onValueChange = {
                inputState as TextAnalysisInputState.Typing
                inputState.input.value = it
            },
            maxLines = 4,
            placeholderText = stringResource(Res.string.text_analysis_input_placeholder),
            decorationPaddings = PaddingValues(
                top = Dimens.ContentPadding,
                start = Dimens.ContentPadding,
                end = Dimens.ContentPadding
            ),
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

            Crossfade(
                targetState = isLoading,
                modifier = Modifier
                    .size(Dimens.IconButton)
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

    val categoryRow: @Composable (String) -> Unit = { title ->
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = Dimens.SpacingBig)
        )
    }

    val clickableRow: @Composable (String, String?) -> Unit = { title, subtitle ->

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceDim)
                .clickable { }
                .padding(horizontal = Dimens.SpacingBig, vertical = Dimens.SpacingMid)
        ) {

            Icon(Icons.Outlined.Check, null)

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                subtitle?.also {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

        }

    }

    MultiplatformDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.text_analysis_configuration_title)) },
        content = {
            categoryRow(stringResource(Res.string.text_analysis_configuration_analysis_provider))
            clickableRow("Ichiran", stringResource(Res.string.text_analysis_ichiran_description))
            categoryRow(stringResource(Res.string.text_analysis_configuration_translation_provider))
            clickableRow("Gemini 2.0 Flash", null)
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
                        .offset(Dimens.SpacingBig + Dimens.SpacingTiny + 1.dp)
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
                            Text(
                                text = stringResource(Res.string.text_analysis_action_furigana),
                                modifier = Modifier.weight(1f)
                            )
                            if (furigana) Icon(Icons.Outlined.Check, null)
                            else Icon(Icons.Outlined.Close, null)
                        }
                        AppDropdownMenuItem(
                            onClick = { highlight = !highlight }
                        ) {
                            Icon(Icons.Outlined.BorderColor, null)
                            Text(
                                text = stringResource(Res.string.text_analysis_action_highlight),
                                modifier = Modifier.weight(1f)
                            )
                            if (highlight) Icon(Icons.Outlined.Check, null)
                            else Icon(Icons.Outlined.Close, null)
                        }
                        return@AppDropdownMenu
                        AppDropdownMenuItem(currentMode.switchToSaveWordsMode) {
                            Icon(Icons.Outlined.Bookmarks, null)
                            Text(stringResource(Res.string.text_analysis_action_save_words))
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
                        text = stringResource(
                            Res.string.text_analysis_save_words_counter,
                            wordsCount.toString()
                        ),
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
                            Text(stringResource(Res.string.text_analysis_save_words_cancel))
                        }

                        TextButton(
                            onClick = { },
                            modifier = Modifier.align(Alignment.CenterVertically),
                            enabled = wordsCount > 0
                        ) {
                            Text(stringResource(Res.string.text_analysis_save_words_apply))
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
                        text = stringResource(
                            Res.string.text_analysis_save_letters_counter,
                            count.toString()
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = Dimens.SpacingMid),
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
                        Text(stringResource(Res.string.text_analysis_save_letters_select_kanji))
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
                            Text(stringResource(Res.string.text_analysis_save_letters_cancel))
                        }

                        TextButton(
                            onClick = { },
                            modifier = Modifier.align(Alignment.CenterVertically),
                            enabled = count > 0
                        ) {
                            Text(stringResource(Res.string.text_analysis_save_letters_apply))
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
    translation: String,
    saveWord: (TextAnalysisNode.Word) -> Unit
) {

    when (displayMode) {
        is TextAnalysisContentMode.WordsDisplay -> {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.SpacingMid)
            ) {
                nodeList.forEach {
                    AnalysisNode(
                        node = it,
                        displayMode = displayMode,
                        saveWord = saveWord
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
private fun RowScope.AnalysisNode(
    node: TextAnalysisNode,
    displayMode: TextAnalysisContentMode.WordsDisplay,
    saveWord: (TextAnalysisNode.Word) -> Unit
) {
    when (node) {
        is TextAnalysisNode.Word -> WordNode(node, displayMode, saveWord)

        is TextAnalysisNode.Text -> {
            Text(
                text = node.value,
                modifier = Modifier.alignByBaseline()
            )
        }

        is TextAnalysisNode.Compound -> {
            node.words.forEach { AnalysisNode(it, displayMode, saveWord) }
        }

        is TextAnalysisNode.Error -> Column(
            modifier = Modifier
                .alignByBaseline()
                .width(IntrinsicSize.Max)
        ) {

            Text(text = node.text ?: stringResource(Res.string.text_analysis_word_parse_error))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.SpacingTiny)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
            )

        }

        is TextAnalysisNode.AlternativeGroup -> {
            val displayNode = node.nodeList.firstOrNull()
            displayNode?.let { AnalysisNode(it, displayMode, saveWord) }
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
    displayMode: TextAnalysisContentMode.WordsDisplay,
    saveWord: (TextAnalysisNode.Word) -> Unit
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
                node = node,
                saveWord = saveWord
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
        ) {

            when {
                node.reading.furigana != null &&
                        displayMode is TextAnalysisContentMode.Browse &&
                        displayMode.furigana.value -> FuriganaText(node.reading.furigana)

                else -> Text(node.text)
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
private fun WordDetailsPopup(
    showPopup: MutableState<Boolean>,
    node: TextAnalysisNode.Word,
    saveWord: (TextAnalysisNode.Word) -> Unit
) {
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
                    .sizeIn(
                        minWidth = Dimens.PopupMinWidth,
                        maxWidth = Dimens.PopupMaxSize,
                        maxHeight = Dimens.PopupMaxSize
                    )
                    .verticalScroll(scrollState)
                    .padding(vertical = Dimens.ContentPaddingSmall)
                    .padding(start = Dimens.ContentPaddingSmall),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
            ) {

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMid)
                ) {
                    val reading = node.dictionaryReading ?: node.reading
                    Text(
                        text = formattedVocabStringReading(
                            kanaReading = reading.kanaReading,
                            kanjiReading = reading.kanjiReading
                        ),
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
                            .clickable { saveWord(node) }
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
                                stringResource(Res.string.text_analysis_history_title)
                            }

                            else -> {
                                stringResource(Res.string.text_analysis_title)
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

                        var showConfigurationDialog by remember { mutableStateOf(false) }
                        if (showConfigurationDialog) {
                            TextAnalysisConfigurationDialog(
                                onDismissRequest = { showConfigurationDialog = false }
                            )
                        }

                        IconButton(
                            onClick = { showConfigurationDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = null,
                            )
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
                                            text = stringResource(Res.string.text_analysis_history_title),
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
