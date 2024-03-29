package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.app_state.DailyGoalConfiguration
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.getBottomLineShape
import ua.syt0r.kanji.presentation.common.resources.string.resolveString

@Composable
fun DailyGoalDialog(
    configuration: DailyGoalConfiguration,
    onDismissRequest: () -> Unit,
    onUpdateConfiguration: (DailyGoalConfiguration) -> Unit
) {

    val strings = resolveString { dailyGoalDialog }

    MultiplatformDialog(onDismissRequest) {

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = strings.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 10.dp).padding(top = 10.dp)
            )

            Text(
                text = strings.message,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            val enabledValue = remember {
                mutableStateOf(configuration.enabled)
            }
            val learnValue = remember {
                mutableStateOf(configuration.learnLimit.toString())
            }
            val reviewValue = remember {
                mutableStateOf(configuration.reviewLimit.toString())
            }

            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(bottom = 20.dp, top = 10.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(strings.enabledLabel, Modifier.weight(1f))
                    Switch(
                        checked = enabledValue.value,
                        onCheckedChange = { enabledValue.value = it }
                    )
                }
                Spacer(Modifier.height(6.dp))
                InputRow(Icons.Default.LocalLibrary, strings.studyLabel, learnValue)
                Spacer(Modifier.height(20.dp))
                InputRow(Icons.Default.Cached, strings.reviewLabel, reviewValue)
            }

            Text(
                text = strings.noteMessage,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Row(
                modifier = Modifier.align(Alignment.End).padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = strings.cancelButton)
                }

                TextButton(
                    onClick = {
                        val updatedConfig = DailyGoalConfiguration(
                            enabled = enabledValue.value,
                            learnLimit = learnValue.value.toInt(),
                            reviewLimit = reviewValue.value.toInt()
                        )
                        onUpdateConfiguration(updatedConfig)
                    },
                    enabled = !learnValue.value.isInputInvalid() &&
                            !reviewValue.value.isInputInvalid()
                ) {
                    Text(text = strings.applyButton)
                }
            }

        }

    }

}

@Composable
private fun InputRow(icon: ImageVector, label: String, input: MutableState<String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
        )

        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )

        BasicTextField(
            value = input.value,
            onValueChange = { input.value = it },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.weight(2f)
                .border(
                    width = 2.dp,
                    color = if (input.value.isInputInvalid()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    shape = getBottomLineShape(2.dp)
                )
                .padding(4.dp)
        )

    }
}

private fun String.isInputInvalid(): Boolean {
    return toIntOrNull().let { it == null || it < 0 }
}