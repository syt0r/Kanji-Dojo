package ua.syt0r.kanji.presentation.screen.main.screen.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(
    availableTabs: List<HomeScreenTab>,
    selectedTabState: State<HomeScreenTab>,
    onTabSelected: (HomeScreenTab) -> Unit,
    screenTabContent: @Composable () -> Unit
) {

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )

        },
        bottomBar = {

            NavigationBar(tonalElevation = 0.dp) {

                val selectedTab = selectedTabState.value

                availableTabs.forEach { tab ->

                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            Icon(
                                painter = painterResource(id = tab.iconResId),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(tab.titleResId)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White
                        )
                    )

                }
            }
        }

    ) {
        Box(
            modifier = Modifier.padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding()
            )
        ) {

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