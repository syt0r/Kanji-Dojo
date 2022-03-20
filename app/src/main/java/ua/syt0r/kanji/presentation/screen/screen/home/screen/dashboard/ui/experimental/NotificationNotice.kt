package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.experimental

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily

@Composable
fun NotificationNotice(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {

    Column {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "Train Regularily",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .wrapContentSize(
                        align = Alignment.CenterStart
                    ),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = stylizedFontFamily,
                fontWeight = FontWeight.Bold
            )

            Icon(
                painter = painterResource(R.drawable.ic_baseline_close_24),
                contentDescription = null,
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(vertical = 16.dp, horizontal = 24.dp)
                    .size(24.dp)
                    .wrapContentSize()
            )

        }

        Row {

            Text(
                text = "Regular training are blablabla to success. Enable training reminder in settings",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .wrapContentSize(
                        align = Alignment.CenterStart
                    ),
                style = MaterialTheme.typography.bodySmall
            )

        }

    }

}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        NotificationNotice(
            onDismiss = {},
            onAccept = {}
        )
    }
}
