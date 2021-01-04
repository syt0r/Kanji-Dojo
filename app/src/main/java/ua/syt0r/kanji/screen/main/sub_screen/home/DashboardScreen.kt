package ua.syt0r.kanji.screen.main.sub_screen.home

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.screen.theme.KanjiDrawerTheme

data class DashboardScreenData(
    val kanjiToReview: Int
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    KanjiDrawerTheme {
        DashboardScreen(
            data = DashboardScreenData(
                kanjiToReview = 135
            )
        )
    }

}

@Composable
fun DashboardScreen(
    data: DashboardScreenData
) {

    ScrollableColumn {

        DashboardCardItem(
            title = "Learning Progress"
        ) {

            Row(
                modifier = Modifier
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    )
            ) {

                val progressBarSize = 80.dp
                val progressBarStrokeWidth = 8.dp

                Box {
                    CircularProgressIndicator(
                        modifier = Modifier.size(progressBarSize),
                        strokeWidth = progressBarStrokeWidth,
                        color = MaterialTheme.colors.secondary,
                        progress = 1f
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(progressBarSize),
                        strokeWidth = progressBarStrokeWidth,
                        color = MaterialTheme.colors.primary,
                        progress = 0.66f
                    )
                    Text(
                        text = "N5",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

            }

            Divider()

            Row(
                modifier = Modifier
                    .clickable {}
                    .padding(24.dp)
            ) {

                Text(
                    text = "Review",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = data.kanjiToReview.toString(),
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

            }

        }

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