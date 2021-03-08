package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.ui.theme.stylizedFontFamily
import java.time.LocalDate
import kotlin.random.Random

@Preview(showSystemUi = true)
@Composable
fun PracticeCalendarPreview() {
    PracticeCalendar(
        daysWithPractice = (0..5).map {
            val randomDayOfMonth = Random.nextInt(
                1,
                LocalDate.now().lengthOfMonth() + 1
            )
            LocalDate.now().withDayOfMonth(randomDayOfMonth)
        }
    )
}

@Composable
fun PracticeCalendar(
    daysWithPractice: List<LocalDate> = emptyList()
) {

    val firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1)
    val calendarFirstDay = firstDayOfCurrentMonth.minusDays(
        (firstDayOfCurrentMonth.dayOfWeek.value.toLong() + 7 - 1) % 7
    )

    val lastDayOfCurrentMonth = LocalDate.now().run { withDayOfMonth(lengthOfMonth()) }

    val calendarDays = mutableListOf<LocalDate>()

    var firstDayOfWeek = calendarFirstDay
    while (firstDayOfWeek < lastDayOfCurrentMonth) {
        for (i in 0..6) {
            calendarDays.add(firstDayOfWeek.plusDays(i.toLong()))
        }
        firstDayOfWeek = firstDayOfWeek.plusDays(7)
    }

    Column {

        Row {

            "月火水木金土日".toCharArray().forEach { char ->

                Text(
                    char.toString(),
                    modifier = Modifier.weight(1f),
                    fontFamily = stylizedFontFamily,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )

            }

        }

        calendarDays.chunked(7).forEach { week ->
            Row {
                week.forEach { date ->

                    Box(modifier = Modifier.weight(1f)) {

                        val isPracticeDate = daysWithPractice.contains(date)

                        Text(
                            date.dayOfMonth.toString(),
                            modifier = Modifier
                                .size(30.dp)
                                .run {
                                    if (!isPracticeDate) this
                                    else background(
                                        color = Color.Red,
                                        shape = CircleShape
                                    )
                                }
                                .align(Alignment.Center)
                                .wrapContentSize(Alignment.Center),
                            color = when {
                                isPracticeDate -> Color.White
                                date == LocalDate.now() -> Color.Red
                                else -> Color.Unspecified
                            },
                            fontWeight = FontWeight.Medium,
                            fontFamily = stylizedFontFamily,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }


                }

            }
        }
    }

}