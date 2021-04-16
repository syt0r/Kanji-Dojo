package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.ui.screen.screen.home.HomeScreenContent
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui.LinearProgressWithNumber
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui.PracticeCalendar
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme

@Composable
fun GeneralDashboardScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Column {

            Text(
                text = "Your monthly progress",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PracticeCalendar()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Continue were you left",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "前回から続く",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Custom Deck 1",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(Modifier.height(IntrinsicSize.Max)) {

                Text(
                    text = "Study progress",
                    fontSize = 14.sp,
                    modifier = Modifier.width(100.dp)
                )

                LinearProgressWithNumber(
                    progress = 0.75f,
                    modifier = Modifier.fillMaxSize()
                )

            }

            Row(Modifier.height(IntrinsicSize.Max)) {

                Text(
                    text = "Review",
                    fontSize = 14.sp,
                    modifier = Modifier.width(100.dp)
                )

                LinearProgressWithNumber(
                    progress = 0.75f,
                    modifier = Modifier.fillMaxSize(),
                    primaryProgressColor = Color.Blue
                )

            }

            Row {

                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {

                    Text(text = "Study")

                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {

                    Text(text = "Review")

                }

            }

        }

    }

}

@Preview(showSystemUi = true)
@Composable
fun GeneralDashboardScreenContentPreview() {

    KanjiDojoTheme {
        GeneralDashboardScreen()
    }

}

@Preview(showSystemUi = true)
@Composable
fun GeneralDashboardScreenPreview() {

    KanjiDojoTheme {

        HomeScreenContent {
            GeneralDashboardScreen()
        }

    }

}
