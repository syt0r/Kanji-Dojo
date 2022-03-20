package ua.syt0r.kanji.presentation.screen.screen.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.home.data.HomeScreenTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(
    tabs: List<HomeScreenTab>,
    selectedTab: HomeScreenTab,
    onTabSelected: (HomeScreenTab) -> Unit,
    screenTabContent: @Composable () -> Unit
) {

    Scaffold(
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            HomeBottomBar(tabs, selectedTab) {
                onTabSelected.invoke(it)
            }
        }
    ) {

        Box(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        ) {

            screenTabContent.invoke()

        }

    }

}

@Composable
private fun HomeTopBar() {

    SmallTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        }
    )

}


@Preview(group = "topbar", showBackground = true)
@Composable
fun TopBarPreview() {

    AppTheme {
        HomeTopBar()
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmptyHomeScreenContentPreview() {

    AppTheme {
        HomeScreenUI(
            tabs = HomeScreenTab.values().toList(),
            selectedTab = HomeScreenTab.DASHBOARD,
            onTabSelected = {},
            screenTabContent = {}
        )
    }

}