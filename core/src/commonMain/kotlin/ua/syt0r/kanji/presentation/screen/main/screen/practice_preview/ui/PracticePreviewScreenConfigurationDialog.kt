package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.icon.Help
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.MultiplatformPopup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.FilterOption
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticePreviewScreenConfigurationDialog(
    configuration: PracticePreviewScreenConfiguration,
    onDismissRequest: () -> Unit = {},
    onApplyConfiguration: (PracticePreviewScreenConfiguration) -> Unit = {}
) {

    var selectedPracticeType by rememberSaveable {
        mutableStateOf(configuration.practiceType)
    }
    var selectedFilterOption by rememberSaveable {
        mutableStateOf(configuration.filterOption)
    }
    var selectedSortOption by rememberSaveable {
        mutableStateOf(configuration.sortOption)
    }
    var isDescending by rememberSaveable {
        mutableStateOf(configuration.isDescending)
    }

    MultiplatformDialog(
        onDismissRequest = onDismissRequest
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, bottom = 10.dp)
        ) {

            Text(
                text = resolveString { practicePreview.screenConfigDialog.title },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 8.dp
                )
            )

            Text(
                text = resolveString { practicePreview.screenConfigDialog.practiceType },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 8.dp
                )
            )

            AutoBreakRow(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.CenterStart),
                horizontalItemSpacing = 8.dp,
                horizontalAlignment = Alignment.Start
            ) {
                PracticeType.values().forEach {
                    FilterChip(
                        selected = it == selectedPracticeType,
                        onClick = { selectedPracticeType = it },
                        label = { Text(resolveString(it.titleResolver)) },
                    )
                }
            }

            Text(
                text = resolveString { practicePreview.screenConfigDialog.filter },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 8.dp
                )
            )

            AutoBreakRow(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.CenterStart),
                horizontalItemSpacing = 8.dp,
                horizontalAlignment = Alignment.Start
            ) {
                FilterOption.values().forEach {
                    FilterChip(
                        selected = it == selectedFilterOption,
                        onClick = { selectedFilterOption = it },
                        label = { Text(text = resolveString(it.titleResolver)) }
                    )
                }
            }

            Text(
                text = resolveString { practicePreview.screenConfigDialog.sorting },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 8.dp
                )
            )


            SortOption.values().forEach {

                val rippleColor = LocalRippleTheme.current.defaultColor()
                val rippleAlpha = LocalRippleTheme.current.rippleAlpha()

                val rowColor by animateColorAsState(
                    targetValue = if (it == selectedSortOption)
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
                            if (selectedSortOption == it) isDescending = !isDescending
                            else selectedSortOption = it
                        },
                    verticalAlignment = Alignment.CenterVertically
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = onDismissRequest) {
                    Text(text = resolveString { practicePreview.screenConfigDialog.buttonCancel })
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        onApplyConfiguration(
                            PracticePreviewScreenConfiguration(
                                practiceType = selectedPracticeType,
                                filterOption = selectedFilterOption,
                                sortOption = selectedSortOption,
                                isDescending = isDescending
                            )
                        )
                    }
                ) {
                    Text(text = resolveString { practicePreview.screenConfigDialog.buttonApply })
                }
            }

        }

    }

}


//@Preview
//@Composable
//private fun Preview() {
//    AppTheme {
//        PracticePreviewScreenConfigurationDialog(configuration = PracticePreviewScreenConfiguration())
//    }
//}