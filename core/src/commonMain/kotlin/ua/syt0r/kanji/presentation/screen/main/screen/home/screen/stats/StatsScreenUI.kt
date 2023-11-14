package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenContract.ScreenState
import kotlin.random.Random

@Composable
fun StatsScreenUI(
    state: State<ScreenState>
) {

    AnimatedContent(
        targetState = state.value,
        modifier = Modifier.fillMaxSize()
    ) {

        when (it) {
            ScreenState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
            }

            is ScreenState.Loaded -> {
                LoadedState(it)
            }
        }

    }


}

@Composable
private fun LoadedState(screenState: ScreenState.Loaded) {

    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentSize(Alignment.TopCenter)
            .padding(horizontal = 20.dp)
            .widthIn(max = 400.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Today",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.weight(2f)) {
                Column(Modifier.padding(20.dp)) {
                    Text("0s", style = MaterialTheme.typography.displayMedium)
                    Text("Time spent on review")
                }
            }

            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(20.dp)) {
                    Text("0", style = MaterialTheme.typography.displayMedium)
                    Text("Reviews")
                }
            }
        }


        Text(
            text = "This week",
            style = MaterialTheme.typography.titleLarge,
//            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LastWeekStudy(
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Text(
            text = "Total",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(Modifier.weight(1f)) {
                Column(Modifier.padding(20.dp)) {
                    Text("0", style = MaterialTheme.typography.displayMedium)
                    Text("Reviews")
                }
            }
            Card(Modifier.weight(2f)) {
                Column(Modifier.padding(20.dp)) {
                    Text("0s", style = MaterialTheme.typography.displayMedium)
                    Text("Time spent studying")
                }
            }

        }

//        Text(
//            text = "No data yet",
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
//        )
//
//        Text(
//            text = "Calendar",
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )

//        YearCalendarUninterrupted(
//            year = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
//        )

//        Text("Total times practiced: 223")
//        Text("Total characters reviewed: 125")
//        Text("Total character reviews: 3450")
//        Text("Total time spent reviewing: 365 days")

    }
}

@Composable
private fun WeekIndicators() {

    Row(Modifier.height(100.dp)) {

        (1..7).forEach {
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(it.toString(), style = MaterialTheme.typography.titleLarge)
                val color =
                    if (Random.nextBoolean()) MaterialTheme.colorScheme.primary else Color.LightGray
                Canvas(Modifier.fillMaxWidth().height(6.dp)) {
                    val path = Path().apply {
                        val shiftWidth = size.width / 8
                        moveTo(shiftWidth, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width - shiftWidth, size.height)
                        lineTo(0f, size.height)
                    }
                    drawPath(path, color)
                }
            }
        }

    }
}

@Composable
private fun Ranking() {
    Text(
        text = "Character difficulty ranking",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    val rankingContent: @Composable RowScope.() -> Unit = {
        Text(
            text = Random.nextInt(1, 10).toString(),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = PreviewKanji.randomKanji(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        )
        Icon(
            imageVector = Icons.Default.Whatshot,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 10.dp)
//                    .alpha(if (it == 5) 1f else 0f)
        )
    }

    (5 downTo 1).forEach {
        if (it == 5) {
            Card {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rankingContent()
                }
            }
        } else {

            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rankingContent()
            }
        }
    }
}

@Composable
private fun YearCalendarUninterrupted(
    year: Int
) {

    val yearStartDate = LocalDate(year, 1, 1)

    val yearDays = sequence<LocalDate> {
        var currentDate = yearStartDate
        do {
            yield(currentDate)
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        } while (currentDate.year == year)
    }.toList()

    val firstWeekDays = 7 - yearDays.first().dayOfWeek.isoDayNumber + 1
    val lastWeekDays = yearDays.last().dayOfWeek.isoDayNumber

    LazyHorizontalGrid(
        rows = GridCells.Adaptive(12.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.height(12.dp * 7 + 7.dp)
    ) {

        if (firstWeekDays != 7) {
            item(span = { GridItemSpan(7 - firstWeekDays) }) { }
        }

        items(yearDays) {

            val color = when {
                it == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date -> {
                    MaterialTheme.extraColorScheme.success
                }

                else -> Color.LightGray
            }

            Box(
                modifier = Modifier
                    .background(color)
                    .size(12.dp)
            )
        }

        if (lastWeekDays != 7) {
            item(span = { GridItemSpan(7 - lastWeekDays) }) { }
        }

    }

}

@Composable
private fun YearCalendarMonthly(
    year: Int
) {

    val monthContent: @Composable (Int) -> Unit = { monthNum ->
        val monthStart = LocalDate(year, monthNum, 1)
        val monthDays = mutableListOf<LocalDate>()

        var date = monthStart
        do {
            monthDays.add(date)
            date = date.plus(1, DateTimeUnit.DAY)
        } while (date.month.value == monthNum)

        Row(modifier = Modifier.padding(0.dp)) {
            sequence {
                val firstWeekDays = 7 - monthStart.dayOfWeek.isoDayNumber + 1
                yield(monthDays.take(firstWeekDays))
                monthDays.drop(firstWeekDays).chunked(7).forEach {
                    yield(it)
                }
            }.forEach { weekDays ->
                Column {
                    val firstDayOfWeek = weekDays.first().dayOfWeek.isoDayNumber
                    if (firstDayOfWeek != 1) {
                        Box(
                            Modifier.size(width = 12.dp, height = 12.dp * (firstDayOfWeek - 1))
                        )
                    }
                    weekDays.forEach {
                        Box(
                            modifier = Modifier.background(Color.LightGray).size(12.dp)
                        )
                    }
                }
            }
        }

    }

    AutoBreakRow(horizontalAlignment = Alignment.Start) {
        (1..12).forEach {
            Column {
                Text(it.toString())
                monthContent(it)
            }
        }
    }

}

private val DayLabels = listOf("月", "火", "水", "木", "金", "土", "日")

@Composable
private fun MonthCalendar() {

    val currentDay = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val firstDayOfMonth = currentDay.run { LocalDate(year, month, 1) }
    val firstDay = firstDayOfMonth.minus(
        DatePeriod(days = firstDayOfMonth.dayOfWeek.isoDayNumber - 1)
    )

    val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH)
        .minus(1, DateTimeUnit.DAY)
    val lastDay = lastDayOfMonth.plus(
        value = 7 - lastDayOfMonth.dayOfWeek.isoDayNumber,
        unit = DateTimeUnit.DAY
    )

    Column {
        Row {
            DayLabels.forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f).aspectRatio(1f).wrapContentSize(),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        var weekStart = firstDay
        do {

            Row {
                (0 until 7).forEach {
                    val day = weekStart.plus(it, DateTimeUnit.DAY)
                    Text(
                        text = day.dayOfMonth.toString(),
                        modifier = Modifier.weight(1f).aspectRatio(1f).wrapContentSize(),
                        color = when {
                            currentDay == day -> MaterialTheme.colorScheme.primary
                            day.month == currentDay.month -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        }
                    )
                }
            }

            weekStart = weekStart.plus(1, DateTimeUnit.WEEK)
        } while (weekStart.month == currentDay.month)

        Row(modifier = Modifier.align(Alignment.End)) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.KeyboardArrowLeft, null)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.KeyboardArrowRight, null)
            }
        }
    }

}
