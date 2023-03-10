package ua.syt0r.kanji.presentation.common

import androidx.compose.runtime.Composable

@Composable
expect fun MultiplatformDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
)