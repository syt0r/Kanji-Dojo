package ua.syt0r.kanji

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.ui.HomeScreenUI

fun main(args: Array<String>) = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

enum class Screen { Home, About }

@Composable
private fun App() {
    AppTheme {
        val tab = remember { mutableStateOf(HomeScreenTab.Default) }

        var screenStack by remember { mutableStateOf(listOf<Screen>(Screen.Home)) }
        val currentScreen = screenStack.last()

        when (currentScreen) {
            Screen.Home -> {
                HomeScreenUI(
                    availableTabs = HomeScreenTab.values().toList(),
                    selectedTabState = tab,
                    onTabSelected = { tab.value = it }
                ) {
                    Button(
                        onClick = { screenStack = screenStack.plus(Screen.About) },
                        modifier = Modifier.fillMaxSize().wrapContentSize()
                    ) {
                        Text("About")
                    }
                }
            }
            Screen.About -> {
                AboutScreenUI(
                    onUpButtonClick = { screenStack = screenStack.dropLast(1) }
                )
            }
        }

    }
}
