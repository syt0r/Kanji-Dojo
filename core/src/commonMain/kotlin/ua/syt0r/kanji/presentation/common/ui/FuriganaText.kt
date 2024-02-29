package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import kotlin.math.max


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
    val density = LocalDensity.current

    return furiganaString.compounds.asSequence()
        .mapIndexed { index, furiganaAnnotatedCharacter ->
            val annotation = furiganaAnnotatedCharacter.annotation
                ?: return@mapIndexed index to null

            val textMeasures = AnnotatedString(furiganaAnnotatedCharacter.text)
                .let { textMeasurer.measure(it, contentTextStyle) }
                .size

            val furiganaMeasures = AnnotatedString(annotation)
                .let { textMeasurer.measure(it, annotationTextStyle, maxLines = 1) }
                .size

            // Making item a bit larger, exact size get clipped sometimes, especially on desktop
            val itemWidthPx = max(textMeasures.width, furiganaMeasures.width) * 1.1f
            val itemHeightPx = textMeasures.height + furiganaMeasures.height * 1.1f

            index to InlineTextContent(
                placeholder = Placeholder(
                    width = with(density) { itemWidthPx.toSp() },
                    height = with(density) { itemHeightPx.toSp() },
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
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Text(
            text = annotation,
            style = annotationTextStyle,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = text,
            style = contentTextStyle,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
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
        modifier = Modifier.fillMaxSize().clickable { onCLick(text) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = annotation,
            style = annotationTextStyle,
            maxLines = 1,
            modifier = Modifier.weight(1f).wrapContentSize(Alignment.BottomCenter)
        )
        Text(
            text = text,
            style = contentTextStyle
        )
    }
}