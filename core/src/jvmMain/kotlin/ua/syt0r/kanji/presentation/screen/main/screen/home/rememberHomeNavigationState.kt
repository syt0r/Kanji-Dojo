package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab

@Composable
actual fun rememberNewHomeNavigationState(): NewHomeNavigationState {
    val tabState = remember { mutableStateOf<HomeScreenTab>(HomeScreenTab.Default) }
    return remember { MultiplatformHomeNavigationState(tabState) }
}

class MultiplatformHomeNavigationState(
    override val selectedTab: MutableState<HomeScreenTab>
) : NewHomeNavigationState {
    override fun navigate(tab: HomeScreenTab) {
        selectedTab.value = tab
    }
}

@Composable
actual fun NewHomeNavigationContent(
    homeNavigationState: NewHomeNavigationState,
    mainNavigationState: MainNavigationState
) {
    homeNavigationState as MultiplatformHomeNavigationState
    when (homeNavigationState.selectedTab.value) {
        HomeScreenTab.PRACTICE_DASHBOARD -> {
            Text("Dashboard")
        }
        HomeScreenTab.SEARCH -> {
            Text("Search")
        }
        HomeScreenTab.SETTINGS -> {
            Text("Settings")
            Button(onClick = { mainNavigationState.navigate(MainDestination.About) }) {
                Text("About")
            }
        }
    }
}