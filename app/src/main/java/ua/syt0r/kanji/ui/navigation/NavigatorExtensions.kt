package ua.syt0r.kanji.ui.navigation

import androidx.compose.runtime.Composable

@Composable
fun getNavigator(): NavigationContract.Navigator {
    return NavigationContract.navigator.current
}