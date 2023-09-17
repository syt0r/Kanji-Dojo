package ua.syt0r.kanji.presentation.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
actual fun MultiplatformDialog(
    onDismissRequest: () -> Unit,
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest) {
        Surface(
            modifier = Modifier.clip(shape = RoundedCornerShape(20.dp)),
            color = containerColor
        ) {
            content()
        }

    }
}