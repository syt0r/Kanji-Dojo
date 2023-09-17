package ua.syt0r.kanji.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun MultiplatformDialog(
    onDismissRequest: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
)