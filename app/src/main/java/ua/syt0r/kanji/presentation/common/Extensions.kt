package ua.syt0r.kanji.presentation.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.URLSpan
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.syt0r.kanji.R
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.presentation.common.theme.linkColor


private const val LinkAnnotationTag = "link"
private val HtmlLinkRegex = "<a.*</a>".toRegex()
private val LinkHrefRegex = "".toRegex()

@Composable
fun spannedStringResource(
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
                tag = LinkAnnotationTag,
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
        .filter { it.tag == LinkAnnotationTag }
        .forEach { onUrlClick(it.item) }
}

@Composable
fun CharactersClassification.Kana.getString(): String {
    return when (this) {
        CharactersClassification.Kana.HIRAGANA -> stringResource(R.string.hiragana)
        CharactersClassification.Kana.KATAKANA -> stringResource(R.string.katakana)
    }
}

fun Context.openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        val chooser = Intent.createChooser(intent, getString(R.string.url_activity_chooser_title))
        startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(this, R.string.url_activity_not_found, Toast.LENGTH_SHORT).show()
    }
}

fun SnackbarHostState.showSnackbarFlow(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
): Flow<Unit> {
    return flow { showSnackbar(message, actionLabel, withDismissAction, duration) }
}