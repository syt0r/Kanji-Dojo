package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeConfiguration


@Composable
fun PracticePreviewStudyOptionsDialog(
    configuration: PracticeConfiguration,
    onDismissRequest: () -> Unit = {},
    onApplyConfiguration: (PracticeConfiguration) -> Unit = {}
) {

    var isStudyMode by remember {
        val default = configuration.let { it as? PracticeConfiguration.Writing }?.isStudyMode
        mutableStateOf(default)
    }

    var shuffle by remember { mutableStateOf(configuration.shuffle) }

    MultiplatformDialog(
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
                    text = resolveString { practicePreview.studyOptionsDialog.title },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 8.dp
                    )
                )

                if (isStudyMode != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable { isStudyMode = isStudyMode!!.not() }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = resolveString { practicePreview.studyOptionsDialog.studyMode },
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isStudyMode!!,
                            onCheckedChange = { isStudyMode = isStudyMode!!.not() }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { shuffle = !shuffle }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = resolveString { practicePreview.studyOptionsDialog.shuffle },
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = shuffle, onCheckedChange = { shuffle = !shuffle })
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 20.dp)
                ) {
                    TextButton(
                        onClick = {
                            val updatedConfiguration = when (val isStudy = isStudyMode) {
                                null -> PracticeConfiguration.Reading(shuffle)
                                else -> PracticeConfiguration.Writing(isStudy, shuffle)
                            }
                            onApplyConfiguration(updatedConfiguration)
                        }
                    ) {
                        Text(text = resolveString { practicePreview.studyOptionsDialog.button })
                    }
                }

            }
        }
    }
}

//@Preview
//@Composable
//private fun Preview() {
//    AppTheme {
//        PracticePreviewStudyOptionsDialog(
//            configuration = PracticeConfiguration.Writing(true, true)
//        )
//    }
//}