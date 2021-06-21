package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ItemContainer
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme
import ua.syt0r.kanji.ui.theme.stylizedFontFamily

@Composable
fun ContinuePreviousPractice(
    practiceSetName: String,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {

            Text(
                text = "Continue・続く",
                style = MaterialTheme.typography.h6,
                fontFamily = stylizedFontFamily,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = practiceSetName,
                fontFamily = stylizedFontFamily,
            )

        }

        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_navigate_next_24),
            contentDescription = null,
            modifier = Modifier
                .padding(24.dp)
                .size(24.dp)
        )

    }

}

@Preview(showSystemUi = true)
@Composable
private fun ContinuePreviousPracticePreview() {

    KanjiDojoTheme {
        ItemContainer {
            ContinuePreviousPractice(practiceSetName = "Custom 1") {}
        }

    }

}