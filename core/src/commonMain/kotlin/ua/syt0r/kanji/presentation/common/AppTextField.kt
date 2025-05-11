package ua.syt0r.kanji.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import ua.syt0r.kanji.presentation.common.theme.Dimens

private val PlaceholderOffset = Dimens.SpacingTiny

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    placeholderText: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    disabledColor: Color = textColor.copy(alpha = 0.38f),
    trailingContent: @Composable (() -> Unit)? = null,
    decorationPaddings: PaddingValues = PaddingValues(
        horizontal = Dimens.ContentPadding,
        vertical = Dimens.SpacingMid
    ),
    modifier: Modifier = Modifier
) {

    val inputTextStyle = textStyle.copyCentered().copy(
        color = if (readOnly) disabledColor else textColor
    )

    BasicTextField(
        value = value,
        readOnly = readOnly,
        onValueChange = onValueChange,
        maxLines = maxLines,
        decorationBox = { content ->

            val showPlaceholder = value.isEmpty() && placeholderText != null

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                        .padding(decorationPaddings)
                        .let { if (showPlaceholder) it.offset(-PlaceholderOffset) else it },
                ) {
                    if (showPlaceholder) {
                        Text(
                            text = placeholderText,
                            style = inputTextStyle.copy(color = disabledColor),
                            modifier = Modifier.offset(PlaceholderOffset)
                        )
                    }
                    content()
                }

                trailingContent?.let { Box { it() } }

            }
        },
        textStyle = inputTextStyle,
        cursorBrush = SolidColor(inputTextStyle.color),
        modifier = modifier
    )

}