package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.resources.string.LocalStrings
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(
    availableTabs: List<HomeScreenTab>,
    selectedTabState: State<HomeScreenTab>,
    onTabSelected: (HomeScreenTab) -> Unit,
    screenTabContent: @Composable () -> Unit
) {

    if (LocalOrientation.current == Orientation.Landscape) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            NavigationRail(
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                availableTabs.forEach { tab ->
                    NavigationRailItem(
                        selected = tab == selectedTabState.value,
                        onClick = { onTabSelected(tab) },
                        icon = { Icon(tab.imageVector, null) },
                        label = { Text(text = tab.titleResolver.invoke(LocalStrings.current)) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Surface { screenTabContent.invoke() }

        }

    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(LocalStrings.current.homeTitle) }
                )
            },
            bottomBar = {
                NavigationBar(tonalElevation = 0.dp) {
                    availableTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selectedTabState.value,
                            onClick = { onTabSelected(tab) },
                            icon = { Icon(tab.imageVector, null) },
                            label = { Text(text = tab.titleResolver.invoke(LocalStrings.current)) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White)
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                screenTabContent.invoke()
            }

        }

    }

}
