package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.textDp
import ua.syt0r.kanji.presentation.common.ui.AutoSizeText
import ua.syt0r.kanji.presentation.common.ui.FancyLoading
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenContract.ScreenState
import kotlin.math.ceil

@Composable
fun StatsScreenUI(
    state: State<ScreenState>
) {

    AnimatedContent(
        targetState = state.value,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = Modifier.fillMaxSize()
    ) {

        when (it) {
            ScreenState.Loading -> {
                FancyLoading(modifier = Modifier.fillMaxSize().wrapContentSize())
            }

            is ScreenState.Loaded -> {
                LoadedState(it)
            }
        }

    }


}

@Composable
private fun LoadedState(screenState: ScreenState.Loaded) {

    val strings = resolveString { stats }

    Column(
        modifier = Modifier.fillMaxSize()
            .wrapContentWidth()
            .padding(horizontal = 20.dp)
            .widthIn(max = 400.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Header(
            text = strings.monthCalendarTitle(screenState.today)
        )

        MonthCalendar(
            today = screenState.today,
            reviewDates = screenState.yearlyPractices
        )

        Header(text = strings.todayTitle)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoCard(
                title = strings.formattedDuration(screenState.todayTimeSpent),
                subtitle = strings.timeSpentTitle
            )

            InfoCard(
                title = screenState.todayReviews.toString(),
                subtitle = strings.reviewsCountTitle
            )
        }

        Header(text = strings.yearTitle)

        YearCalendarUninterrupted(
            year = screenState.today.year,
            reviewDates = screenState.yearlyPractices
        )

        val yearTotalDays = LocalDate(screenState.today.year + 1, 1, 1)
            .minus(1, DateTimeUnit.DAY)
            .dayOfYear

        Text(
            text = strings.yearDaysPracticedLabel(screenState.yearlyPractices.size, yearTotalDays),
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Header(text = strings.totalTitle)

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                title = strings.formattedDuration(screenState.totalTimeSpent),
                subtitle = strings.timeSpentTitle
            )
            InfoCard(
                title = screenState.totalReviews.toString(),
                subtitle = strings.reviewsCountTitle
            )
        }

        Spacer(Modifier.height(20.dp))

    }
}

@Composable
private fun Header(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun RowScope.InfoCard(title: String, subtitle: String) {
    Card(Modifier.weight(1f)) {
        Column(Modifier.padding(20.dp)) {
            AutoSizeText(
                text = title,
                style = MaterialTheme.typography.displayMedium
            )
            Text(subtitle)
        }
    }
}

@Composable
private fun YearCalendarUninterrupted(
    year: Int,
    reviewDates: Map<LocalDate, Int>
) {

    val yearStartDate = LocalDate(year, 1, 1)
    val yearEndDate = LocalDate(year + 1, 1, 1).minus(1, DateTimeUnit.DAY)

    val daysInYear = yearEndDate.dayOfYear
    val daysInFirstWeekLastYear = yearStartDate.dayOfWeek.isoDayNumber - 1
    val daysInLastWeekNextYear = 7 - yearEndDate.dayOfWeek.isoDayNumber
    val totalDays = daysInYear + daysInFirstWeekLastYear + daysInLastWeekNextYear

    val weeks = totalDays / 7

    val boxSize = 12.dp
    val margins = 2.dp

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val calendarWidth = boxSize * weeks + margins * (weeks - 1)

    val initialScrollDp = calendarWidth * (today.dayOfYear / totalDays.toFloat()) - 200.dp
    val initialScrollPx = with(LocalDensity.current) { initialScrollDp.roundToPx() }

    val defaultColor = MaterialTheme.colorScheme.surfaceVariant
    val practicedColor = MaterialTheme.colorScheme.primary
    val todayColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(
        modifier = Modifier.horizontalScroll(rememberScrollState(initialScrollPx))
            .size(
                width = calendarWidth,
                height = boxSize * 7 + margins * 6
            )
    ) {

        val size = Size(boxSize.toPx(), boxSize.toPx())
        var weekStart = yearStartDate

        do {
            val weekIndex = ceil(yearStartDate.daysUntil(weekStart) / 7f)
            val weekDays = (weekStart.dayOfWeek.isoDayNumber..7)
                .mapIndexed { index, i -> weekStart.plus(index, DateTimeUnit.DAY) }

            for (day in weekDays) {
                val dayIndex = day.dayOfWeek.isoDayNumber - 1
                val dayOffset = Offset(
                    x = boxSize.toPx() * weekIndex + margins.toPx() * weekIndex,
                    y = boxSize.toPx() * dayIndex + margins.toPx() * dayIndex
                )
                drawRoundRect(
                    color = if (reviewDates.contains(day)) practicedColor else defaultColor,
                    topLeft = dayOffset,
                    size = size,
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
                if (day == today) {
                    val centerOffset = boxSize.toPx() / 2
                    drawCircle(
                        color = todayColor,
                        radius = size.minDimension / 4,
                        center = dayOffset.plus(Offset(centerOffset, centerOffset))
                    )
                }
            }

            weekStart = weekStart.plus(weekDays.size, DateTimeUnit.DAY)
        } while (weekStart.year == year)

    }

}

private val DayLabels = listOf("月", "火", "水", "木", "金", "土", "日")

sealed interface MonthCalendarItem {

    val dayNumber: Int

    data class DayFromOtherMonth(override val dayNumber: Int) : MonthCalendarItem

    data class DayFromCurrentMonth(
        override val dayNumber: Int,
        val isPracticed: Boolean,
        val isToday: Boolean
    ) : MonthCalendarItem

}

@Composable
private fun MonthCalendar(today: LocalDate, reviewDates: Map<LocalDate, Int>) {

    val firstDayOfMonth = today.run { LocalDate(year, month, 1) }
    val firstDay = firstDayOfMonth.minus(
        DatePeriod(days = firstDayOfMonth.dayOfWeek.isoDayNumber - 1)
    )
    val month = firstDayOfMonth.month

    val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH)
        .minus(1, DateTimeUnit.DAY)
    val lastDay = lastDayOfMonth.plus(
        value = 7 - lastDayOfMonth.dayOfWeek.isoDayNumber,
        unit = DateTimeUnit.DAY
    )

    val gridItems = mutableListOf<MonthCalendarItem>()

    var day = firstDay
    while (day <= lastDay) {
        if (day.month != month) {
            gridItems.add(MonthCalendarItem.DayFromOtherMonth(dayNumber = day.dayOfMonth))
        } else {
            gridItems.add(
                MonthCalendarItem.DayFromCurrentMonth(
                    dayNumber = day.dayOfMonth,
                    isPracticed = reviewDates.containsKey(day),
                    isToday = day == today
                )
            )
        }
        day = day.plus(1, DateTimeUnit.DAY)
    }

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {


        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DayLabels.forEach {
                Text(
                    text = it,
                    modifier = Modifier.size(45.dp).wrapContentSize(),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.textDp
                )
            }
        }

        val indicatorColor = MaterialTheme.colorScheme.primary

        gridItems.chunked(7).forEach { week ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                week.forEach {
                    Text(
                        text = it.dayNumber.toString(),
                        modifier = Modifier
                            .drawBehind {
                                if (it !is MonthCalendarItem.DayFromCurrentMonth) {
                                    return@drawBehind
                                }
                                if (it.isToday) {
                                    drawCircle(indicatorColor, style = Stroke(2.dp.toPx()))
                                }
                                if (it.isPracticed) {
                                    drawCircle(
                                        color = indicatorColor,
                                        radius = 4.dp.toPx(),
                                        center = Offset(size.width / 2, size.height / 8)
                                    )
                                }
                            }
                            .size(45.dp)
                            .wrapContentSize(),
                        color = when (it) {
                            is MonthCalendarItem.DayFromCurrentMonth -> {
                                MaterialTheme.colorScheme.onSurface
                            }

                            is MonthCalendarItem.DayFromOtherMonth -> {
                                MaterialTheme.colorScheme.onSurface.copy(0.5f)
                            }
                        },
                        fontSize = 17.textDp
                    )
                }
            }
        }
    }

}
