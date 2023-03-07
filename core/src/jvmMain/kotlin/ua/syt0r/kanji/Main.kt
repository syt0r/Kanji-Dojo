package ua.syt0r.kanji

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.ui.HomeScreenUI

fun main(args: Array<String>) = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
private fun App() {
    AppTheme {
        val tab = remember { mutableStateOf(HomeScreenTab.Default) }
        HomeScreenUI(
            availableTabs = HomeScreenTab.values().toList(),
            selectedTabState = tab,
            onTabSelected = { tab.value = it }
        ) {
            Box(Modifier.fillMaxSize().wrapContentSize().size(40.dp).background(Color.Black))
        }
    }
}
