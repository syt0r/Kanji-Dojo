package ua.syt0r.kanji.ui.screen.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.getViewModel
import ua.syt0r.kanji.R
import ua.syt0r.kanji.ui.screen.screen.home.HomeScreenContract.Screen
import ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.GeneralDashboardScreen
import ua.syt0r.kanji.ui.screen.screen.home.screen.settings.SettingsScreen
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardScreen
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme
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
    currentScreenState: State<Screen>,
    onScreenSelected: (Screen) -> Unit
) {

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomBar(currentScreenState) { onScreenSelected.invoke(it) } }
    ) {

        Box(
            modifier = Modifier.padding(bottom = it.bottom)
        ) {
            when (currentScreenState.value) {
                Screen.GENERAL_DASHBOARD -> {
                    GeneralDashboardScreen()
                }
                Screen.WRITING_DASHBOARD -> {
                    WritingDashboardScreen()
                }
                Screen.SETTINGS -> {
                    SettingsScreen()
                }
            }
        }

    }

}

@Composable
private fun HomeTopBar() {

    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
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

            BottomNavigationItem(
                icon = {
                    Text(
                        text = screen.stylizedText,
                        fontSize = 26.sp,
                        fontFamily = stylizedFontFamily
                    )
                },
                label = { Text(text = stringResource(id = screen.textResId)) },
                selected = screen == currentScreenState.value,
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
