package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui.ContinuePreviousPractice
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui.DayStrike
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme
import ua.syt0r.kanji.ui.theme.red

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GeneralDashboardScreen() {

    ScrollableColumn {

        val dismissState = rememberDismissState(DismissValue.Default) { true }
        val isDismissed = dismissState.run {
            isDismissed(DismissDirection.EndToStart) || isDismissed(DismissDirection.StartToEnd)
        }

        if (!isDismissed) {

            SwipeToDismiss(
                state = dismissState,
                background = {}
            ) {
                DayStrike(days = 5)
            }

        }

        ContinuePreviousPractice("Test set 1")

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {

            Card(
                modifier = Modifier.size(68.dp),
                backgroundColor = red,
                shape = RoundedCornerShape(12.dp)
            ) {

                Text(
                    text = "1",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    color = Color.White,
                    fontSize = 22.sp,
                )

            }

        }

    }

}

@Preview(showSystemUi = true)
@Composable
fun GeneralDashboardScreenPreview() {

    KanjiDojoTheme {

        GeneralDashboardScreen()

    }

}
