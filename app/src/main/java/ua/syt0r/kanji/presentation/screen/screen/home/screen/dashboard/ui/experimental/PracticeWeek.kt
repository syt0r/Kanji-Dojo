package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.experimental

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.random.Random

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    PracticeWeek(
        firstDate = LocalDate.now().plusDays(Random.nextLong(0, 7)),
        daysToHighlight = (0 until Random.nextInt(1, 7))
            .map { LocalDate.now().plusDays(Random.nextLong(0, 7)) }
            .toSet()
    )
}

private val kanjiDays = "月火水木金土日".toCharArray().toList()
private val dayOfWeekToKanji = DayOfWeek.values().zip(kanjiDays).toMap()

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PracticeWeek(
    firstDate: LocalDate,
    daysToHighlight: Set<LocalDate>
) {

    Column {

        Row {

            val visible = remember { mutableStateOf(true) }

            (0 until 7).forEach { dayShift ->

                val date = firstDate.plusDays(dayShift.toLong())

                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = dayShift * 100,
                        )
                    ),
                    modifier = Modifier.clickable { visible.value = !visible.value },
                ) {

                    Day(
                        text = dayOfWeekToKanji.getValue(date.dayOfWeek).toString(),
                        isHighlighted = daysToHighlight.contains(date)
                    )

                }

            }

        }

    }

}

@Composable
private fun Day(text: String, isHighlighted: Boolean) {

    Text(
        text,
        modifier = Modifier
            .size(30.dp)
            .run {
                if (isHighlighted) background(
                    color = MaterialTheme.colors.secondary,
                    shape = CircleShape
                ) else background(
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                )
            }
            .wrapContentSize(Alignment.Center),
        color = MaterialTheme.colors.primary,
        fontFamily = stylizedFontFamily,
        fontSize = 24.sp,
        textAlign = TextAlign.Center
    )

}