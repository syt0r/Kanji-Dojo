package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.items

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
import ua.syt0r.kanji.core.user_data.model.RecentPractice
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily
import java.time.LocalDateTime

@Composable
fun WritingTraining(
    recentPractice: RecentPractice,
    onClick: () -> Unit
) {

    DashboardItem {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onClick() }
            ) {

                Text(
                    text = "Writing Training",
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
                    painter = painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .size(24.dp)
                        .wrapContentSize()
                )

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onClick() }
            ) {

                Text(
                    text = recentPractice.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "続く",
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = stylizedFontFamily,
                    fontWeight = FontWeight.Bold
                )

            }

        }

    }

}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        WritingTraining(RecentPractice(0, "Test", LocalDateTime.now())) { }
    }
}