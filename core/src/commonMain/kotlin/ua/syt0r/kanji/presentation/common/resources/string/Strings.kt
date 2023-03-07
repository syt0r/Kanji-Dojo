package ua.syt0r.kanji.presentation.common.resources.string

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf


typealias StringResolveScope = Strings.() -> String

@Composable
fun resolveString(resolveScope: StringResolveScope): String {
    return LocalStrings.current.resolveScope()
}

val LocalStrings = compositionLocalOf<Strings> { EnglishStrings }

interface Strings {
    val homeTitle: String
    val homeTabDashboard: String
    val homeTabSearch: String
    val homeTabSettings: String
}

