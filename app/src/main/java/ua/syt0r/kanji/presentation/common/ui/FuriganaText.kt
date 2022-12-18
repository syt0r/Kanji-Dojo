package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.core.kanji_data.data.FuriganaString
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import java.lang.Integer.max

@OptIn(ExperimentalTextApi::class)
@Composable
fun FuriganaText(
    furiganaString: FuriganaString,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    annotationTextStyle: TextStyle = MaterialTheme.typography.bodySmall
) {

    val scaleToSp: Float = with(LocalDensity.current) { fontScale * density }

    val textMeasurer = rememberTextMeasurer()

    val inlineContent = furiganaString.compounds.asSequence()
        .mapIndexed { index, furiganaAnnotatedCharacter ->

            if (furiganaAnnotatedCharacter.annotation == null) return@mapIndexed index to null

            val textMeasures = textMeasurer.measure(
                AnnotatedString(furiganaAnnotatedCharacter.character),
                textStyle
            ).size

            val furiganaMeasures = AnnotatedString(furiganaAnnotatedCharacter.annotation)
                .let { textMeasurer.measure(it, annotationTextStyle) }
                .size

            index to InlineTextContent(
                placeholder = Placeholder(
                    width = (max(textMeasures.width, furiganaMeasures.width) / scaleToSp).sp,
                    height = ((textMeasures.height + furiganaMeasures.height) / scaleToSp).sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
                ),
                children = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = furiganaAnnotatedCharacter.annotation,
                            style = annotationTextStyle,
                            maxLines = 1
                        )
                        Text(
                            text = furiganaAnnotatedCharacter.character,
                            style = textStyle
                        )

                    }
                }
            )
        }
        .filter { it.second != null }
        .map { it.first.toString() to it.second!! }
        .toMap()

    BasicText(
        text = buildAnnotatedString {
            furiganaString.compounds.forEachIndexed { index, furigana ->
                if (furigana.annotation == null) append(furigana.character)
                else appendInlineContent(index.toString())
            }
        },
        inlineContent = inlineContent,
        style = textStyle,
        modifier = modifier
    )

}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        FuriganaText(
            furiganaString = buildFuriganaString {
                append("イランコントラ")
                append("事", "じ")
                append("件", "けん")
                append(" - accident of sdfq fqoe fqeiof foewij fewifoefwio")
            },
            modifier = Modifier.width(200.dp)
        )
    }
}