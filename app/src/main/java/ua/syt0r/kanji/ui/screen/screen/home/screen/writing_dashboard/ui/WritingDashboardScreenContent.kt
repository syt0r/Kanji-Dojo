package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.ui

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.DashboardProgressItemData
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme

@Preview(showBackground = true)
@Composable
fun WritingDashboardScreenContentPreview() {
    KanjiDojoTheme {
        WritingDashboardScreenContent(
            state = mutableStateOf(WritingDashboardScreenContract.State.Loading)
        )
    }
}

@Composable
fun WritingDashboardScreenContent(
    state: State<WritingDashboardScreenContract.State>
) {
    ScrollableColumn {

        DashboardCardItem(
            title = "Training Sets"
        ) {

            val trainingSets = listOf(
                "JLPT",
                "Custom"
            )

            trainingSets.forEach {

                Divider()

                Row(
                    modifier = Modifier
                        .clickable { }
                        .padding(24.dp)
                ) {

                    Text(
                        text = it,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = vectorResource(
                            id = R.drawable.ic_baseline_keyboard_arrow_right_24
                        )
                    )

                }

            }

        }

    }
}

@Composable
fun DashboardCardItem(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Box(modifier = Modifier.padding(10.dp)) {

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp
        ) {

            Column {

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 24.dp,
                            bottom = 24.dp
                        )
                )

                content()

            }

        }
    }

}

@Composable
fun DashboardProgressItem(
    data: DashboardProgressItemData
) {

    val progressBarSize = 80.dp
    val progressBarStrokeWidth = 8.dp

    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(progressBarSize),
            strokeWidth = progressBarStrokeWidth,
            color = MaterialTheme.colors.secondary,
            progress = 1f
        )
        CircularProgressIndicator(
            modifier = Modifier.size(progressBarSize),
            strokeWidth = progressBarStrokeWidth,
            color = MaterialTheme.colors.primaryVariant,
            progress = data.current.toFloat() / data.max
        )
        Text(
            text = data.text,
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colors.primaryVariant,
            fontWeight = FontWeight.Bold
        )
    }

}