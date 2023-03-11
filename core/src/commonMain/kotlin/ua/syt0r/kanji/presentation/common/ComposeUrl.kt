package ua.syt0r.kanji.presentation.common

import androidx.compose.runtime.Composable

interface UrlHandler {
    fun openInBrowser(url: String)
}

@Composable
expect fun rememberUrlHandler(): UrlHandler