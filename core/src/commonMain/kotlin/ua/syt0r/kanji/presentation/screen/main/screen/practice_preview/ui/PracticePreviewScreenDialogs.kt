package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.icon.Help
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.MultiplatformPopup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.FilterOption
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewLayout
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption


@Composable
fun PracticePreviewScreenPracticeTypeDialog(
    practiceType: PracticeType,
    onDismissRequest: () -> Unit,
    onApplyConfiguration: (PracticeType) -> Unit
) {

    var selectedPracticeType by rememberSaveable { mutableStateOf(practiceType) }

    PracticePreviewScreenBaseDialog(
        title = resolveString { practicePreview.practiceTypeDialog.title },
        onDismissRequest = onDismissRequest,
        onApplyClick = { onApplyConfiguration(selectedPracticeType) }
    ) {

        PracticeType.values().forEach {
            SelectableRow(
                isSelected = it == selectedPracticeType,
                onClick = { selectedPracticeType = it }
            ) {
                Icon(
                    imageVector = it.imageVector,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(start = 20.dp)
                        .size(24.dp)
                )
                Text(
                    text = resolveString(it.titleResolver),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }

    }

}


@Composable
fun PracticePreviewScreenFilterOptionDialog(
    filter: FilterOption,
    onDismissRequest: () -> Unit,
    onApplyConfiguration: (FilterOption) -> Unit
) {

    var selected by rememberSaveable { mutableStateOf(filter) }

    PracticePreviewScreenBaseDialog(
        title = resolveString { practicePreview.filterDialog.title },
        onDismissRequest = onDismissRequest,
        onApplyClick = { onApplyConfiguration(selected) }
    ) {

        FilterOption.values().forEach {
            SelectableRow(
                isSelected = it == selected,
                onClick = { selected = it }
            ) {
                Icon(
                    imageVector = it.imageVector,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(start = 20.dp)
                        .size(24.dp)
                )
                Text(
                    text = resolveString(it.titleResolver),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }

    }

}

@Composable
fun PracticePreviewScreenSortDialog(
    onDismissRequest: () -> Unit,
    onApplyClick: (sortOption: SortOption, isDescending: Boolean) -> Unit,
    sortOption: SortOption,
    isDesc: Boolean
) {

    var selectedSortOption by rememberSaveable { mutableStateOf(sortOption) }
    var isDescending by rememberSaveable { mutableStateOf(isDesc) }

    PracticePreviewScreenBaseDialog(
        title = resolveString { practicePreview.sortDialog.title },
        onDismissRequest = onDismissRequest,
        onApplyClick = { onApplyClick(selectedSortOption, isDescending) }
    ) {

        SortOption.values().forEach {

            SelectableRow(
                isSelected = it == selectedSortOption,
                onClick = {
                    if (selectedSortOption == it) isDescending = !isDescending
                    else selectedSortOption = it
                }
            ) {

                Text(
                    text = resolveString(it.titleResolver),
                    modifier = Modifier.padding(start = 14.dp)
                )

                var showHint by remember { mutableStateOf(false) }

                IconButton(onClick = { showHint = true }) {
                    Icon(ExtraIcons.Help, null)
                }

                MultiplatformPopup(
                    expanded = showHint,
                    onDismissRequest = { showHint = false }
                ) {
                    Text(
                        text = resolveString(it.hintResolver),
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .widthIn(max = 200.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (it == selectedSortOption) {
                    val rotation by animateFloatAsState(
                        targetValue = if (isDescending) 90f else 270f
                    )
                    IconButton(onClick = { isDescending = !isDescending }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.graphicsLayer(rotationZ = rotation)
                        )
                    }
                }

            }

        }
    }

}


@Composable
fun PracticePreviewLayoutDialog(
    layout: PracticePreviewLayout,
    kanaGroups: Boolean,
    onDismissRequest: () -> Unit,
    onApplyConfiguration: (layout: PracticePreviewLayout, kanaMode: Boolean) -> Unit
) {

    var selectedLayout by remember { mutableStateOf(layout) }
    var selectedKanaGroups by remember { mutableStateOf(kanaGroups) }

    PracticePreviewScreenBaseDialog(
        title = resolveString { practicePreview.layoutDialog.title },
        onDismissRequest = onDismissRequest,
        onApplyClick = { onApplyConfiguration(selectedLayout, selectedKanaGroups) }
    ) {

        PracticePreviewLayout.values().forEach {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(MaterialTheme.shapes.large)
                    .clickable { selectedLayout = it }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedLayout = it }) {
                    Icon(
                        imageVector = when (selectedLayout) {
                            it -> Icons.Default.RadioButtonChecked
                            else -> Icons.Default.RadioButtonUnchecked
                        },
                        contentDescription = null
                    )
                }
                Text(resolveString(it.titleResolver))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable { selectedKanaGroups = !selectedKanaGroups }
                .padding(start = 20.dp, end = 10.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(text = resolveString { practicePreview.layoutDialog.kanaGroupsTitle })
                Text(
                    text = resolveString { practicePreview.layoutDialog.kanaGroupsSubtitle },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(checked = selectedKanaGroups, onCheckedChange = { selectedKanaGroups = it })
        }

    }

}


@Composable
private fun PracticePreviewScreenBaseDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onApplyClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    MultiplatformDialog(
        onDismissRequest = onDismissRequest
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 8.dp
                )
            )

            content()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = onDismissRequest) {
                    Text(text = resolveString { practicePreview.dialogCommon.buttonCancel })
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onApplyClick) {
                    Text(text = resolveString { practicePreview.dialogCommon.buttonApply })
                }

            }

        }

    }

}

@Composable
private fun SelectableRow(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {

    val rippleColor = LocalRippleTheme.current.defaultColor()
    val rippleAlpha = LocalRippleTheme.current.rippleAlpha()

    val rowColor by animateColorAsState(
        targetValue = when (isSelected) {
            true -> rippleColor.copy(alpha = rippleAlpha.pressedAlpha)
            false -> rippleColor.copy(alpha = 0f)
        }
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(rowColor)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )

}
