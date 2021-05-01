package ua.syt0r.kanji.ui.screen.screen.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.di.getViewModel
import ua.syt0r.kanji.ui.screen.screen.home.HomeScreenContract.Screen
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.GeneralDashboardScreen
import ua.syt0r.kanji.ui.screen.screen.home.screen.search.SearchScreen
import ua.syt0r.kanji.ui.screen.screen.home.screen.settings.SettingsScreen
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardScreen
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme
import ua.syt0r.kanji.ui.theme.secondary
import ua.syt0r.kanji.ui.theme.stylizedFontFamily

@Composable
fun HomeScreen(
    viewModel: HomeScreenContract.ViewModel = getViewModel<HomeViewModel>()
) {

    HomeScreenContent(
        currentScreenState = viewModel.currentScreen.observeAsState(Screen.DEFAULT),
        onScreenSelected = { viewModel.selectScreen(it) }
    )

}

@Composable
fun HomeScreenContent(
    currentScreenState: State<Screen> = mutableStateOf(Screen.GENERAL_DASHBOARD),
    onScreenSelected: (Screen) -> Unit = {},
    screenTabContent: @Composable () -> Unit = { HomeScreenTabContent(currentScreenState) }
) {

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomBar(currentScreenState) { onScreenSelected.invoke(it) } }
    ) {

        Box(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        ) {

            screenTabContent.invoke()

        }

    }

}

@Composable
private fun HomeScreenTabContent(currentScreenState: State<Screen>) {
    Crossfade(targetState = currentScreenState.value) {
        when (it) {
            Screen.GENERAL_DASHBOARD -> {
                GeneralDashboardScreen()
            }
            Screen.WRITING_DASHBOARD -> {
                WritingDashboardScreen()
            }
            Screen.SEARCH -> {
                SearchScreen()
            }
            Screen.SETTINGS -> {
                SettingsScreen()
            }
        }
    }
}

@Composable
private fun HomeTopBar() {

    TopAppBar(
        title = {
            Text(
                text = "漢字・道場",
                fontFamily = stylizedFontFamily
            )
        },
        backgroundColor = MaterialTheme.colors.primary
    )

}

@Composable
private fun HomeBottomBar(
    currentScreenState: State<Screen>,
    onScreenSelected: (Screen) -> Unit
) {
    BottomAppBar(
        modifier = Modifier.background(Color.Blue)
    ) {

        Screen.values().forEach { screen ->

            val isSelected = screen == currentScreenState.value

            BottomNavigationItem(
                icon = {
                    Text(
                        text = screen.stylizedText,
                        modifier = Modifier
                            .let {
                                if (isSelected) it.background(
                                    color = secondary,
                                    shape = CircleShape
                                )
                                else it
                            }
                            .padding(
                                vertical = 4.dp,
                                horizontal = 12.dp
                            ),
                        color = if (isSelected) MaterialTheme.colors.primary
                        else MaterialTheme.colors.onPrimary,
                        fontSize = 24.sp,
                        fontFamily = stylizedFontFamily
                    )
                },
                selected = isSelected,
                onClick = { onScreenSelected.invoke(screen) }
            )

        }

    }
}


@Preview(showBackground = true)
@Composable
fun TopBarPreview() {

    KanjiDojoTheme {
        HomeTopBar()
    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {

    KanjiDojoTheme {
        HomeBottomBar(
            currentScreenState = mutableStateOf(Screen.WRITING_DASHBOARD),
            onScreenSelected = { }
        )
    }

}

@Preview(showBackground = true, heightDp = 600)
@Composable
fun EmptyHomeScreenContentPreview() {

    KanjiDojoTheme {
        HomeScreenContent(
            currentScreenState = mutableStateOf(Screen.GENERAL_DASHBOARD),
            onScreenSelected = {},
            screenTabContent = {}
        )
    }

}
