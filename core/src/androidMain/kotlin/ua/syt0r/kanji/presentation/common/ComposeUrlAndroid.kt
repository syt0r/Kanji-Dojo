package ua.syt0r.kanji.presentation.common

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import ua.syt0r.kanji.presentation.common.resources.string.resolveString

@Composable
actual fun rememberUrlHandler(): UrlHandler {
    val context = LocalContext.current
    val pickerMessage = resolveString { urlPickerMessage }
    val errorMessage = resolveString { urlPickerErrorMessage }
    return remember {
        object : UrlHandler {
            override fun openInBrowser(url: String) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    val chooser = Intent.createChooser(intent, pickerMessage)
                    context.startActivity(chooser)
                } catch (e: Exception) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}