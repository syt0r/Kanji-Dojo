package ua.syt0r.kanji.presentation.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight

private const val linkTag = "link"

@OptIn(ExperimentalTextApi::class)
fun AnnotatedString.Builder.appendLink(text: String, url: String) {
    withAnnotation(tag = linkTag, annotation = url) {
        withStyle(
            SpanStyle(color = Color(0xff0054d7), fontWeight = FontWeight.Bold)
        ) {
            append(text)
        }
    }
}

fun AnnotatedString.detectUrlClick(position: Int, onUrlClick: (String) -> Unit) {
    getStringAnnotations(position, position)
        .filter { it.tag == linkTag }
        .forEach { onUrlClick(it.item) }
}