package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.MultiplatformDialog

@Composable
fun DailyGoalDialog(
    onDismissRequest: () -> Unit
) {

    MultiplatformDialog(onDismissRequest) {

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Daily Goal",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 10.dp).padding(top = 10.dp)
            )

            Text(
                text = "Impacts quick practice characters count and reminder notification appearance",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            val learnValue = remember { mutableStateOf("6") }
            val reviewValue = remember { mutableStateOf("12") }

            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                InputRow(Icons.Default.NewReleases, "Study", learnValue)
                InputRow(Icons.Default.Cached, "Review", reviewValue)

            }

            Text(
                text = "Note: Writing and reading reviews are counted separately towards the goal",
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
                    Text(text = "Cancel")
                }

                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = "Apply")
                }
            }


        }

    }

}

@Composable
private fun InputRow(icon: ImageVector, label: String, input: MutableState<String>) {
    val isError = input.value.toIntOrNull().let { it == null || it < 0 }

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
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    shape = getBottomLineShape(2.dp)
                )
                .padding(4.dp)
        )

    }
}

@Composable
private fun getBottomLineShape(strokeThickness: Dp): Shape {
    val strokeThicknessPx = with(LocalDensity.current) { strokeThickness.toPx() }
    return GenericShape { size, _ ->
        moveTo(0f, size.height)
        lineTo(size.width, size.height)
        lineTo(size.width, size.height - strokeThicknessPx)
        lineTo(0f, size.height - strokeThicknessPx)
    }
}