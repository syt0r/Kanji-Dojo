package ua.syt0r.kanji.presentation.screen.screen.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.screen.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.common.theme.secondary
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily

@Composable
fun HomeScreenUI(
    tabs: List<HomeScreenTab>,
    initialSelectedTab: MutableState<HomeScreenTab>,
    onTabSelected: (HomeScreenTab) -> Unit,
    screenTabContent: @Composable () -> Unit
) {

    Scaffold(
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            HomeBottomBar(tabs, initialSelectedTab.value) {
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
    tabs: List<HomeScreenTab>,
    currentlySelectedTab: HomeScreenTab,
    onTabSelected: (HomeScreenTab) -> Unit
) {

    BottomAppBar(
        modifier = Modifier.background(Color.Blue)
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
                                    color = secondary,
                                    shape = CircleShape
                                )
                                else it
                            }
                            .padding(
                                vertical = 4.dp,
                                horizontal = 16.dp
                            )
                    ) {

                        Text(
                            text = "Info",
                            color = if (isSelected) MaterialTheme.colors.primary
                            else MaterialTheme.colors.onPrimary,
                            fontSize = 11.sp,
                            fontFamily = stylizedFontFamily
                        )

                        Text(
                            text = stringResource(id = tab.titleResId),
                            color = if (isSelected) MaterialTheme.colors.primary
                            else MaterialTheme.colors.onPrimary,
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

@Preview(group = "topbar", showBackground = true)
@Composable
fun DarkTopBarPreview() {

    KanjiDojoTheme(darkTheme = true) {
        HomeTopBar()
    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {

//    KanjiDojoTheme {
//        HomeBottomBar(
//
//        )
//    }

}

@Preview(showBackground = true, heightDp = 600, showSystemUi = true)
@Composable
fun EmptyHomeScreenContentPreview() {

//    KanjiDojoTheme {
//        HomeScreenUI(
//
//        )
//    }

}