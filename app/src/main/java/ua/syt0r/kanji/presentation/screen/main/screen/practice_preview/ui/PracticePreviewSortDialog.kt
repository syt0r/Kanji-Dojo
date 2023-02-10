package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.CustomDropdownMenu
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption


@Composable
fun PracticePreviewSortDialog(
    currentSortConfiguration: SortConfiguration,
    onDismissRequest: () -> Unit = {},
    onApplySort: (SortConfiguration) -> Unit = {}
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


@Preview
@Composable
private fun Preview() {
    AppTheme {
        PracticePreviewSortDialog(currentSortConfiguration = SortConfiguration())
    }
}