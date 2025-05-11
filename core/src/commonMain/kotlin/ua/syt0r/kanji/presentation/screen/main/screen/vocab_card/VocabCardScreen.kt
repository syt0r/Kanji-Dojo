package ua.syt0r.kanji.presentation.screen.main.screen.vocab_card

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ua.syt0r.kanji.core.user_data.database.VocabCardData
import ua.syt0r.kanji.presentation.common.AppDropdownMenu
import ua.syt0r.kanji.presentation.common.AppDropdownMenuItem
import ua.syt0r.kanji.presentation.common.AppListItem
import ua.syt0r.kanji.presentation.common.AppTextField
import ua.syt0r.kanji.presentation.common.clickable
import ua.syt0r.kanji.presentation.common.theme.Dimens
import ua.syt0r.kanji.presentation.common.theme.neutralButtonColors
import ua.syt0r.kanji.presentation.common.ui.FancyLoading
import ua.syt0r.kanji.presentation.dialog.SaveWordDialog
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.vocab_card.VocabCardScreenContract.EditResultStorage
import ua.syt0r.kanji.presentation.screen.main.screen.vocab_card.VocabCardScreenContract.ScreenState

@Composable
fun VocabCardScreen(
    navigationState: MainNavigationState,
    screenMode: VocabCardScreenMode,
    cardData: SuggestedVocabCardData
) {

    LaunchedEffect(Unit) {
        EditResultStorage.resetResult()
    }

    val viewModel = getMultiplatformViewModel<VocabCardScreenContract.ViewModel>(
        screenMode,
        cardData
    )
    val screenState = viewModel.state.collectAsState()
    ScreenUI(
        screenState = screenState,
        navigateBack = { navigationState.navigateBack() },
        setEditResultAndLeave = { mode, cardData ->
            val result = VocabCardEditResult(mode.index, cardData)
            EditResultStorage.setResult(result)
            navigationState.navigateBack()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenUI(
    screenState: State<ScreenState>,
    navigateBack: () -> Unit,
    setEditResultAndLeave: (VocabCardScreenMode.Edit, VocabCardData) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocab Card") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {

        ScreenContent(it, screenState) { screenState ->

            val labelStyle = MaterialTheme.typography.labelMedium
            val textFieldModifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.ContentPaddingSmall)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        screenState.kanjiEnabled.value = screenState.kanjiEnabled.value.not()
                    }
                    .padding(horizontal = Dimens.SpacingBig, vertical = Dimens.SpacingMid)
            ) {

                Text(
                    text = "Kanji Reading",
                    style = labelStyle,
                    modifier = Modifier.weight(1f)
                )

                val icon: ImageVector
                val color: Color

                when {
                    screenState.kanjiEnabled.value -> {
                        icon = Icons.Default.CheckCircle
                        color = MaterialTheme.colorScheme.primary
                    }

                    else -> {
                        icon = Icons.Outlined.Circle
                        color = MaterialTheme.colorScheme.surfaceVariant
                    }
                }

                Icon(imageVector = icon, contentDescription = null, tint = color)

            }

            AppListItem {
                AppTextField(
                    value = screenState.kanji.value,
                    onValueChange = { screenState.kanji.value = it },
                    readOnly = screenState.kanjiEnabled.value.not(),
                    trailingContent = {
                        ReadingSuggestionButton(screenState.kanji, screenState.kanjiOptions)
                    },
                    modifier = textFieldModifier
                )
            }

            AppListItem {
                Text("Kana reading", style = labelStyle)
            }

            AppListItem {
                AppTextField(
                    value = screenState.kana.value,
                    onValueChange = { screenState.kana.value = it },
                    trailingContent = {
                        ReadingSuggestionButton(screenState.kana, screenState.kanaOptions)
                    },
                    modifier = textFieldModifier
                )
            }

            AppListItem {
                Text("Meaning", style = labelStyle)
            }

            AppListItem {
                AppTextField(
                    value = screenState.meaning.value,
                    onValueChange = { screenState.meaning.value = it },
                    trailingContent = {
                        MeaningSuggestionButton(screenState.meaning, screenState.meaningOptions)
                    },
                    modifier = textFieldModifier
                )
            }

            Spacer(Modifier.weight(1f))

            var vocabCardDataForSaving by remember { mutableStateOf<VocabCardData?>(null) }
            vocabCardDataForSaving?.let {
                SaveWordDialog(
                    cardData = it,
                    onDismissRequest = { vocabCardDataForSaving = null },
                    onSaved = navigateBack
                )
            }


            val cardState = screenState.cardState.value

            AppListItem {
                Button(
                    onClick = {
                        cardState as VocabCardEditState.Valid
                        when (val mode = screenState.mode) {
                            is VocabCardScreenMode.Edit -> {
                                setEditResultAndLeave(mode, cardState.cardData)
                            }

                            VocabCardScreenMode.Save -> {
                                vocabCardDataForSaving = cardState.cardData
                            }
                        }

                    },
                    enabled = cardState is VocabCardEditState.Valid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.neutralButtonColors(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    val text = when (screenState.mode) {
                        is VocabCardScreenMode.Edit -> "Edit"
                        VocabCardScreenMode.Save -> "Save"
                    }
                    Text(text)

                }
            }
        }

    }

}

@Composable
private fun ReadingSuggestionButton(
    fieldValue: MutableState<String>,
    options: List<VocabCardReadingSuggestion>
) {

    var expand by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expand = true },
        enabled = options.isNotEmpty()
    ) {
        Icon(Icons.Default.ArrowDropDown, null)

        AppDropdownMenu(
            expanded = expand,
            onDismissRequest = { expand = false }
        ) {
            options.forEach {
                AppDropdownMenuItem(
                    onClick = {
                        fieldValue.value = it.reading
                        expand = false
                    }
                ) {
                    when {
                        it.matchingReadings.isEmpty() -> Text(it.reading)
                        else -> Text("${it.reading} [${it.matchingReadings.joinToString()}]")
                    }

                }
            }
        }
    }

}

@Composable
private fun MeaningSuggestionButton(
    fieldValue: MutableState<String>,
    options: List<String>
) {

    var expand by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expand = true },
        enabled = options.isNotEmpty()
    ) {
        Icon(Icons.Default.ArrowDropDown, null)

        AppDropdownMenu(
            expanded = expand,
            onDismissRequest = { expand = false }
        ) {
            options.forEach {
                AppDropdownMenuItem(
                    onClick = {
                        fieldValue.value = it
                        expand = false
                    }
                ) {
                    Text(it)
                }
            }
        }
    }

}


@Composable
private fun ScreenContent(
    scaffoldPaddings: PaddingValues,
    state: State<ScreenState>,
    content: @Composable ColumnScope.(ScreenState.Loaded) -> Unit
) {
    Crossfade(
        targetState = state.value,
        modifier = Modifier
            .padding(scaffoldPaddings)
            .fillMaxSize()
    ) {

        when (it) {
            ScreenState.Loading -> FancyLoading(Modifier.fillMaxSize().wrapContentWidth())
            is ScreenState.Loaded -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentWidth()
                    .width(Dimens.ScreenWidth)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = Dimens.ContentPadding)
            ) {
                content(it)
            }
        }

    }
}