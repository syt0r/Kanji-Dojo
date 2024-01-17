package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.md_theme_light_primary

@Composable
fun FancyLoading(
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {

        val loadingTransition = rememberInfiniteTransition(label = "trans")

        val periodsToRemove by loadingTransition.animateValue(
            initialValue = 3,
            targetValue = 0,
            typeConverter = Int.VectorConverter,
            animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
            label = ""
        )

        Text(
            text = buildAnnotatedString {
                val text = "Loading..."
                append(text)
                addStyle(
                    style = SpanStyle(Color.Transparent),
                    start = text.length - periodsToRemove,
                    end = text.length
                )
            }
        )

        LinearProgressIndicator(
            modifier = Modifier.width(200.dp),
            color = md_theme_light_primary
        )

    }

}