package ua.syt0r.kanji.presentation.common

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.syt0r.kanji.R
import ua.syt0r.kanji.common.CharactersClassification

@Composable
fun CharactersClassification.Kana.stringResource(): String {
    return when (this) {
        CharactersClassification.Kana.HIRAGANA -> stringResource(R.string.hiragana)
        CharactersClassification.Kana.KATAKANA -> stringResource(R.string.katakana)
    }
}

fun Context.asActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.asActivity()
    else -> null
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