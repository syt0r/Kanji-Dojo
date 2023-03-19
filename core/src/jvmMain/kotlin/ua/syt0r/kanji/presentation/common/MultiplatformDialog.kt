package ua.syt0r.kanji.presentation.common

import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun MultiplatformDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = {
            Surface(
                // Static width to avoid crash when Subcomponent is used inside, e.g. Slider, etc.
                modifier = Modifier.width(360.dp)
            ) { content() }
        }
    )

}