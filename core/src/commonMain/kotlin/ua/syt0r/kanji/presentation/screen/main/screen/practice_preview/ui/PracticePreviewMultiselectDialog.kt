package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.MultiselectPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroup
import java.lang.Integer.max


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

    val selectedCountState = remember { mutableStateOf(availableCharactersCount) }

    MultiplatformDialog(
        onDismissRequest = onDismissRequest
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, bottom = 10.dp)
                .padding(horizontal = 20.dp)
        ) {

            Text(
                text = resolveString { practicePreview.multiselectDialog.title },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = resolveString { practicePreview.multiselectDialog.message })

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
                    text = resolveString { practicePreview.multiselectDialog.selected },
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
                    Text(text = resolveString { practicePreview.multiselectDialog.button })
                }
            }
        }
    }
}

//@Preview
//@Composable
//private fun Preview() {
//    AppTheme {
//        val groups = (1..6).map {
//            PracticeGroup.random(it)
//        }
//        PracticePreviewMultiselectDialog(
//            groups = groups,
//            selectedGroupIndexes = groups.take(groups.size / 2).map { it.index }.toSet()
//        )
//    }
//}