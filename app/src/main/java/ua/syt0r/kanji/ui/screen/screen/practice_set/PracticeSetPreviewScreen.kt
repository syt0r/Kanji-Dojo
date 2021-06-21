package ua.syt0r.kanji.ui.screen.screen.practice_set

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.ui.screen.LocalMainNavigator
import ua.syt0r.kanji.ui.screen.MainContract
import ua.syt0r.kanji.ui.theme.secondary
import ua.syt0r.kanji.ui.theme.stylizedFontFamily

@Composable
fun PracticeSetPreviewScreen(
    navigator: MainContract.Navigation = LocalMainNavigator.current,
    onKanjiClicked: (String) -> Unit = {}
) {

    val n5 =
        "一七万三上下中九二五人今休会何先入八六円出分前北十千午半南友口古右名四国土外多大天女子学安小少山川左年店後手新日時書月木本来東校母毎気水火父生男白百目社空立耳聞花行西見言話語読買足車週道金長間雨電食飲駅高魚"
            .toCharArray()
            .map { it.toString() }

    Loaded(
        kanjiList = n5,
        onKanjiClicked = { kanji -> navigator.navigateToKanjiInfo(kanji) }
    )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Loaded(
    kanjiList: List<String>,
    onKanjiClicked: (String) -> Unit
) {

    ScreenContent(
        kanjiList,
        onKanjiClicked = { onKanjiClicked(it) }
    )

}

@Composable
private fun ScreenContent(
    kanjiList: List<String>,
    onKanjiClicked: (String) -> Unit
) {

    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                backgroundColor = Color.White,
                contentColor = secondary
            ) {
                Text(
                    text = "始",
                    fontFamily = stylizedFontFamily,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {

        Box(
            modifier = Modifier.padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding()
            )
        ) {

            KanjiList(
                kanjiList = kanjiList,
                onKanjiClicked = { onKanjiClicked.invoke(it) }
            )

        }


    }

}

@Composable
private fun TopBar() {

    TopAppBar(
        title = { Text(text = "JLPT N5") },
        navigationIcon = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                    contentDescription = null
                )
            }
        }
    )

}

private const val itemsInRow = 6

@Composable
private fun KanjiList(
    kanjiList: List<String>,
    onKanjiClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {

        items(kanjiList.chunked(itemsInRow)) {

            Row {

                it.forEach { kanji ->

                    KanjiListItem(
                        kanji = kanji,
                        onClick = { onKanjiClicked(kanji) }
                    )

                }

                if (it.size < itemsInRow) {
                    val weight = itemsInRow.toFloat() - it.size
                    Spacer(
                        modifier = Modifier.weight(weight)
                    )
                }

            }

        }

    }
}

@Composable
private fun RowScope.KanjiListItem(
    kanji: String,
    onClick: () -> Unit
) {

    Text(
        text = kanji,
        modifier = Modifier
            .clickable { onClick() }
            .weight(1f)
            .height(50.dp)
            .wrapContentSize(),
        fontSize = 36.sp
    )

}