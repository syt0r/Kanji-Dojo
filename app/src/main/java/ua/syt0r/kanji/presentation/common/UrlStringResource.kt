package ua.syt0r.kanji.presentation.common

import android.text.SpannableString
import android.text.style.URLSpan
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme

@Composable
fun stringResourceWithHtmlUrls(
    @StringRes resId: Int,
    linkColor: Color = MaterialTheme.extraColorScheme.link
): AnnotatedString {
    val string = LocalContext.current.getText(resId)
    val spannableString = SpannableString(string)

    val urlSpans = spannableString.getSpans(0, spannableString.length, URLSpan::class.java)

    return buildAnnotatedString {

        append(spannableString.toString())

        urlSpans.forEach {
            val start = spannableString.getSpanStart(it)
            val end = spannableString.getSpanEnd(it)
            addClickableUrl(it.url, linkColor, start, end)
        }
    }
}
