package ua.syt0r.kanji.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight

interface UrlHandler {
    fun openInBrowser(url: String)
}

@Composable
expect fun rememberUrlHandler(): UrlHandler

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