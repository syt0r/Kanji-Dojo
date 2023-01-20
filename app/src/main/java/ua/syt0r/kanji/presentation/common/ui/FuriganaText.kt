package ua.syt0r.kanji.presentation.common.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.kanji_data.data.FuriganaString
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import java.lang.Integer.max


private const val RubyTagName = "ruby"
private const val RtTagName = "rt"

private fun xmlTagRegex(tagName: String) = "<$tagName>(.*?)</$tagName>".toRegex()

@Composable
fun furiganaStringResource(@StringRes stringResId: Int): FuriganaString {
    val rawText = stringResource(stringResId)
    return buildFuriganaString {

        val rubyRegex = xmlTagRegex(RubyTagName)

        val noRubyTagContent = rawText.split(rubyRegex)
        val rubyTagContents: List<String?> = rubyRegex.findAll(rawText)
            .map { it.groupValues[1] }
            .toList()
            .run {
                if (size != noRubyTagContent.size) plus(null)
                else this
            }

        noRubyTagContent.zip(rubyTagContents).map { (textBeforeRuby, rubyContent) ->

            append(textBeforeRuby)
            if (rubyContent == null) return@map

            val rtRegex = xmlTagRegex(RtTagName)

            val noRtTagContent = rubyContent.split(rtRegex)
            val rtTagsContent = rtRegex.findAll(rubyContent)
                .map { it.groupValues[1] }
                .toList()
                .run {
                    if (size != noRtTagContent.size) plus(null)
                    else this
                }

            noRtTagContent.zip(rtTagsContent).forEach { (content, annotation) ->
                append(content, annotation)
            }

        }

    }

}

@Composable
fun FuriganaText(
    furiganaString: FuriganaString,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
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
                append(furigana.character)
            } else {
                appendInlineContent(index.toString())
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

            if (furiganaAnnotatedCharacter.annotation == null) return@mapIndexed index to null

            val textMeasures = AnnotatedString(furiganaAnnotatedCharacter.character)
                .let { textMeasurer.measure(it, contentTextStyle) }
                .size

            val furiganaMeasures = AnnotatedString(furiganaAnnotatedCharacter.annotation)
                .let { textMeasurer.measure(it, annotationTextStyle) }
                .size

            index to InlineTextContent(
                placeholder = Placeholder(
                    width = (max(textMeasures.width, furiganaMeasures.width) / spToPxScale).sp,
                    height = ((textMeasures.height + furiganaMeasures.height) / spToPxScale).sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
                ),
                children = {
                    inlineContent(
                        furiganaAnnotatedCharacter.character,
                        furiganaAnnotatedCharacter.annotation
                    )
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

@Preview(showBackground = true, locale = "ja")
@Composable
private fun Preview() {
    AppTheme {
        FuriganaText(
            furiganaString = furiganaStringResource(R.string.writing_practice_reading_on),
            modifier = Modifier.width(200.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClickPreview() {

    AppTheme {

        var counter by remember { mutableStateOf(0) }
        var selected: String? by remember { mutableStateOf(null) }

        Column {
            ClickableFuriganaText(
                furiganaString = buildFuriganaString {
                    append("事", "じ")
                    append("イランコントラ")
                    append("事", "じ")
                    append("件", "けん")
                    append(" - accident of sdfq fqoe fqeiof foewij fewifoefwio")
                },
                onClick = {
                    counter++
                    selected = it
                },
                modifier = Modifier.width(200.dp)
            )
            Text(
                text = "${selected}: $counter",
                modifier = Modifier.background(Color.Green)
            )
        }

    }

}