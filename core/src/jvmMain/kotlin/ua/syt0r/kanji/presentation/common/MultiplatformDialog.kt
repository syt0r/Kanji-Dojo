package ua.syt0r.kanji.presentation.common

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ua.syt0r.kanji.presentation.common.theme.AppTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun MultiplatformDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = {
            AppTheme {
                Surface(
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) { content() }
            }
        }
    )

}