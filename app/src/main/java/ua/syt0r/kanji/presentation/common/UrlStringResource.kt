package ua.syt0r.kanji.presentation.common

import android.text.SpannableString
import android.text.style.URLSpan
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import ua.syt0r.kanji.presentation.common.theme.linkColor

private const val UrlAnnotationTag = "url"

@Composable
fun stringResourceWithHtmlUrls(
    @StringRes resId: Int,
    linkColor: Color = MaterialTheme.colorScheme.linkColor()
): AnnotatedString {
    val string = LocalContext.current.getText(resId)
    val spannableString = SpannableString(string)

    val urlSpans = spannableString.getSpans(0, spannableString.length, URLSpan::class.java)

    val linkSpanStyle = SpanStyle(color = linkColor, fontWeight = FontWeight.Bold)

    return buildAnnotatedString {

        append(spannableString)

        urlSpans.forEach {
            val start = spannableString.getSpanStart(it)
            val end = spannableString.getSpanEnd(it)
            addStringAnnotation(
                tag = UrlAnnotationTag,
                annotation = it.url,
                start = start,
                end = end
            )
            addStyle(linkSpanStyle, start, end)

        }
    }
}

fun AnnotatedString.detectUrlClick(position: Int, onUrlClick: (String) -> Unit) {
    getStringAnnotations(position, position)
        .filter { it.tag == UrlAnnotationTag }
        .forEach { onUrlClick(it.item) }
}