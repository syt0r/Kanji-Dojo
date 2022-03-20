package ua.syt0r.kanji.presentation.screen.screen.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily
import ua.syt0r.kanji.presentation.screen.screen.home.data.HomeScreenTab

@Composable
fun HomeBottomBar(
    tabs: List<HomeScreenTab>,
    currentlySelectedTab: HomeScreenTab,
    onTabSelected: (HomeScreenTab) -> Unit
) {

    NavigationBar {

        tabs.forEach { tab ->

            BottomBarItem(
                tab = tab,
                isSelected = tab == currentlySelectedTab,
                onClick = { onTabSelected(tab) }
            )

        }
    }

}

@Composable
private fun RowScope.BottomBarItem(
    tab: HomeScreenTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
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
        }
    )
}

@Deprecated("not used")
@Composable
fun HomeBottomBarOld(
    tabs: List<HomeScreenTab>,
    currentlySelectedTab: HomeScreenTab,
    onTabSelected: (HomeScreenTab) -> Unit
) {

    BottomAppBar(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
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

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {

    AppTheme {
        HomeBottomBar(
            tabs = HomeScreenTab.values().toList(),
            currentlySelectedTab = HomeScreenTab.DASHBOARD,
            onTabSelected = {}
        )
    }

}