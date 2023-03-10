package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.core.kanji_data.data.FuriganaString


@Composable
fun FuriganaText(
    furiganaString: FuriganaString,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = LocalTextStyle.current,
    annotationTextStyle: TextStyle = MaterialTheme.typography.bodySmall
) {

    val coloredTextStyle = textStyle.copy(color)
    val coloredAnnotationStyle = annotationTextStyle.copy(color)

    BasicText(
        text = getFuriganaAnnotatedString(furiganaString),
        inlineContent = getInlineContent(furiganaString, coloredTextStyle, coloredAnnotationStyle),
        style = coloredTextStyle,
        modifier = modifier
    )

}

@Composable
fun ClickableFuriganaText(
    furiganaString: FuriganaString,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    annotationTextStyle: TextStyle = MaterialTheme.typography.bodySmall
) {

    val coloredTextStyle = textStyle.copy(color)
    val coloredAnnotationStyle = annotationTextStyle.copy(color)

    BasicText(
        text = getFuriganaAnnotatedString(furiganaString),
        inlineContent = getInlineContent(
            furiganaString = furiganaString,
            contentTextStyle = coloredTextStyle,
            annotationTextStyle = coloredAnnotationStyle,
            inlineContent = { text, annotation ->
                ClickableInlineFurigana(
                    text = text,
                    annotation = annotation,
                    contentTextStyle = coloredTextStyle,
                    annotationTextStyle = coloredAnnotationStyle,
                    onCLick = onClick
                )
            }
        ),
        style = coloredTextStyle,
        modifier = modifier,
    )

}

private fun getFuriganaAnnotatedString(furiganaString: FuriganaString): AnnotatedString {
    return buildAnnotatedString {
        furiganaString.compounds.forEachIndexed { index, furigana ->
            if (furigana.annotation == null) {
                append(furigana.text)
            } else {
                appendInlineContent(index.toString(), furigana.text)
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun getInlineContent(
    furiganaString: FuriganaString,
    contentTextStyle: TextStyle,
    annotationTextStyle: TextStyle,
    inlineContent: @Composable (text: String, annotation: String) -> Unit = { text, annotation ->
        DefaultInlineFurigana(
            text = text,
            annotation = annotation,
            contentTextStyle = contentTextStyle,
            annotationTextStyle = annotationTextStyle
        )
    }
): Map<String, InlineTextContent> {

    val textMeasurer = rememberTextMeasurer()
    val spToPxScale: Float = with(LocalDensity.current) { fontScale * density }

    return furiganaString.compounds.asSequence()
        .mapIndexed { index, furiganaAnnotatedCharacter ->
            val annotation = furiganaAnnotatedCharacter.annotation
                ?: return@mapIndexed index to null

            val textMeasures = AnnotatedString(furiganaAnnotatedCharacter.text)
                .let { textMeasurer.measure(it, contentTextStyle) }
                .size

            val furiganaMeasures = AnnotatedString(annotation)
                .let { textMeasurer.measure(it, annotationTextStyle) }
                .size

            index to InlineTextContent(
                placeholder = Placeholder(
                    width = (Integer.max(
                        textMeasures.width,
                        furiganaMeasures.width
                    ) / spToPxScale).sp,
                    height = ((textMeasures.height + furiganaMeasures.height) / spToPxScale).sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
                ),
                children = {
                    inlineContent(furiganaAnnotatedCharacter.text, annotation)
                }
            )
        }
        .filter { it.second != null }
        .map { it.first.toString() to it.second!! }
        .toMap()
}

@Composable
private fun DefaultInlineFurigana(
    text: String,
    annotation: String,
    contentTextStyle: TextStyle,
    annotationTextStyle: TextStyle
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = annotation,
            style = annotationTextStyle,
            maxLines = 1
        )
        Text(
            text = text,
            style = contentTextStyle
        )
    }
}

@Composable
private fun ClickableInlineFurigana(
    text: String,
    annotation: String,
    contentTextStyle: TextStyle,
    annotationTextStyle: TextStyle,
    onCLick: (text: String) -> Unit
) {
    Column(
        modifier = Modifier.clickable { onCLick(text) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = annotation,
            style = annotationTextStyle,
            maxLines = 1
        )
        Text(
            text = text,
            style = contentTextStyle
        )
    }
}