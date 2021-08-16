package ua.syt0r.kanji.presentation.screen.screen.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.common.theme.secondary
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar
import ua.syt0r.kanji.presentation.screen.screen.home.data.HomeScreenTab

@Composable
fun HomeScreenUI(
    tabs: List<HomeScreenTab>,
    initialSelectedTab: HomeScreenTab,
    onTabSelected: (HomeScreenTab) -> Unit,
    screenTabContent: @Composable () -> Unit
) {

    Scaffold(
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            HomeBottomBar(tabs, initialSelectedTab) {
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

    CustomTopBar(title = "漢字・道場", upButtonVisible = false)

}

@Composable
private fun HomeBottomBar(
    tabs: List<HomeScreenTab>,
    currentlySelectedTab: HomeScreenTab,
    onTabSelected: (HomeScreenTab) -> Unit
) {

    BottomAppBar(
        modifier = Modifier.background(secondary),
        backgroundColor = secondary,
        contentColor = Color.White
    ) {

        tabs.forEach { tab ->

            val isSelected = tab == currentlySelectedTab

            BottomNavigationItem(
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .let {
                                if (isSelected) it.background(
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                else it
                            }
                            .padding(
                                vertical = 2.dp,
                                horizontal = 16.dp
                            )
                    ) {

                        val textColor = if (isSelected) Color.Black
                        else Color.White

                        Text(
                            text = stringResource(tab.titleResId),
                            color = textColor,
                            fontSize = 11.sp,
                            fontFamily = stylizedFontFamily
                        )

                        Text(
                            text = tab.stylizedTitle,
                            color = textColor,
                            fontSize = 20.sp,
                            fontFamily = stylizedFontFamily
                        )

                    }

                },
                selected = isSelected,
                onClick = { onTabSelected(tab) }
            )

        }

    }
}


@Preview(group = "topbar", showBackground = true)
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
            tabs = HomeScreenTab.values().toList(),
            currentlySelectedTab = HomeScreenTab.DASHBOARD,
            onTabSelected = {}
        )
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmptyHomeScreenContentPreview() {

    KanjiDojoTheme {
        HomeScreenUI(
            tabs = HomeScreenTab.values().toList(),
            initialSelectedTab = HomeScreenTab.DASHBOARD,
            onTabSelected = {},
            screenTabContent = {}
        )
    }

}