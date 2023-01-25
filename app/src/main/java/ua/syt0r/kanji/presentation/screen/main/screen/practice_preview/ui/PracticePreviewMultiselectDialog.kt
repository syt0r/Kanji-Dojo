package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.MultiselectPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import java.lang.Integer.max
import java.time.LocalDateTime


@Composable
fun PracticePreviewMultiselectDialog(
    groups: List<PracticeGroup>,
    selectedGroupIndexes: Set<Int>,
    onDismissRequest: () -> Unit = {},
    onStartClick: (MultiselectPracticeConfiguration) -> Unit = {}
) {

    val availableCharactersCount = groups
        .filter { selectedGroupIndexes.contains(it.index) }
        .sumOf { it.items.size }

    val selectedCountState = remember { mutableStateOf(max(1, availableCharactersCount / 2)) }

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
                    .padding(horizontal = 20.dp)
            ) {

                Text(
                    text = stringResource(R.string.practice_preview_multiselect_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = stringResource(R.string.practice_preview_multiselect_dialog_description))

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = 1.toString())

                    Slider(
                        value = selectedCountState.value.toFloat(),
                        onValueChange = { selectedCountState.value = it.toInt() },
                        steps = availableCharactersCount,
                        valueRange = 1f..availableCharactersCount.toFloat(),
                        modifier = Modifier.weight(1f)
                    )

                    Text(text = availableCharactersCount.toString())

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {

                    Text(
                        text = selectedCountState.value.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.shapes.small
                            )
                            .padding(vertical = 4.dp, horizontal = 10.dp)
                            .wrapContentSize()
                    )

                    Text(
                        text = stringResource(
                            R.string.practice_preview_multiselect_dialog_selected_count_message
                        ),
                        style = MaterialTheme.typography.titleSmall,
                    )

                }

                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(
                        onClick = {
                            onStartClick(
                                MultiselectPracticeConfiguration(
                                    groups,
                                    selectedGroupIndexes,
                                    selectedCountState.value
                                )
                            )
                        }
                    ) {
                        Text(text = stringResource(R.string.practice_preview_multiselect_dialog_confirm))
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
        val groups = (1..6).map {
            PracticeGroup(
                index = it,
                items = (1..6).map { PracticeGroupItem.random() },
                firstDate = LocalDateTime.now(),
                lastDate = LocalDateTime.now(),
                reviewState = CharacterReviewState.values().random()
            )
        }
        PracticePreviewMultiselectDialog(
            groups = groups,
            selectedGroupIndexes = groups.take(groups.size / 2).map { it.index }.toSet()
        )
    }
}