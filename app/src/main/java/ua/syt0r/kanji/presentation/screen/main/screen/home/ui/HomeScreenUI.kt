package ua.syt0r.kanji.presentation.screen.main.screen.home.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.furiganaStringResource
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(
    availableTabs: List<HomeScreenTab>,
    selectedTabState: State<HomeScreenTab>,
    onTabSelected: (HomeScreenTab) -> Unit,
    screenTabContent: @Composable () -> Unit
) {

    val orientation = LocalConfiguration.current.orientation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { FuriganaText(furiganaString = furiganaStringResource(R.string.home_title)) }
            )
        },
        bottomBar = {

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                NavigationBar(tonalElevation = 0.dp) {
                    availableTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selectedTabState.value,
                            onClick = { onTabSelected(tab) },
                            icon = { Icon(painterResource(tab.iconResId), null) },
                            label = { Text(text = stringResource(tab.titleResId)) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White)
                        )
                    }
                }
            }
        }

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                NavigationRail {
                    availableTabs.forEach { tab ->
                        NavigationRailItem(
                            selected = tab == selectedTabState.value,
                            onClick = { onTabSelected(tab) },
                            icon = { Icon(painterResource(tab.iconResId), null) },
                            label = { Text(text = stringResource(tab.titleResId)) }
                        )
                    }
                }
            }

            screenTabContent.invoke()

        }

    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmptyHomeScreenContentPreview() {

    AppTheme {
        HomeScreenUI(
            availableTabs = HomeScreenTab.values().toList(),
            selectedTabState = HomeScreenTab.values().random().run { rememberUpdatedState(this) },
            onTabSelected = {},
            screenTabContent = {}
        )
    }

}