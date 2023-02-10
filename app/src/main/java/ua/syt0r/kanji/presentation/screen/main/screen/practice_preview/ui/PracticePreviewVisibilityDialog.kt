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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.VisibilityConfiguration

@Composable
fun PracticePreviewVisibilityDialog(
    visibilityConfiguration: VisibilityConfiguration,
    onDismissRequest: () -> Unit,
    onApply: (VisibilityConfiguration) -> Unit
) {

    Dialog(
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
                    text = stringResource(R.string.practice_preview_visibility_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                var isReviewOnlyEnabled by remember {
                    mutableStateOf(visibilityConfiguration.reviewOnlyGroups)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { isReviewOnlyEnabled = !isReviewOnlyEnabled }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.practice_preview_visibility_dialog_review_only),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isReviewOnlyEnabled,
                        onCheckedChange = { isReviewOnlyEnabled = !isReviewOnlyEnabled }
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 20.dp)
                ) {
                    TextButton(
                        onClick = { onApply(VisibilityConfiguration(isReviewOnlyEnabled)) }
                    ) {
                        Text(text = stringResource(R.string.practice_preview_visibility_dialog_button))
                    }
                }

            }
        }
    }

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        PracticePreviewVisibilityDialog(
            visibilityConfiguration = VisibilityConfiguration(),
            onDismissRequest = {},
            onApply = {}
        )
    }
}