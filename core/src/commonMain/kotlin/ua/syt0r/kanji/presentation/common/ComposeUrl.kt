package ua.syt0r.kanji.presentation.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle

private val urlSpanBuilder: (Color) -> SpanStyle = {
    SpanStyle(color = it, fontWeight = FontWeight.Bold)
}

@OptIn(ExperimentalTextApi::class)
fun AnnotatedString.Builder.addClickableUrl(
    url: String,
    color: Color,
    startPosition: Int,
    endPosition: Int,
) {
    addStyle(urlSpanBuilder(color), startPosition, endPosition)
    addUrlAnnotation(urlAnnotation = UrlAnnotation(url), startPosition, endPosition)
}

@OptIn(ExperimentalTextApi::class)
fun AnnotatedString.Builder.withClickableUrl(
    url: String,
    color: Color,
    block: AnnotatedString.Builder.() -> Unit
) {
    withAnnotation(
        urlAnnotation = UrlAnnotation(url)
    ) {
        withStyle(urlSpanBuilder(color), block)
    }
}

@OptIn(ExperimentalTextApi::class)
fun AnnotatedString.detectUrlClick(position: Int, onUrlClick: (String) -> Unit) {
    getUrlAnnotations(position, position).forEach { onUrlClick(it.item.url) }
}