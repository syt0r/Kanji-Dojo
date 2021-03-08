package ua.syt0r.kanji.ui.screen.screen.review_set_preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.di.get
import ua.syt0r.kanji.ui.common.Kanji
import ua.syt0r.svg_parser.SvgCommandParser

@Composable
private fun ReviewSetPreviewScreen(
    onKanjiClicked: (String) -> Unit,
    kanjiDataStore: KanjiDataStoreContract.DataStore = get()
) {

    val n5 =
        "一七万三上下中九二五人今休会何先入八六円出分前北十千午半南友口古右名四国土外多大天女子学安小少山川左年店後手新日時書月木本来東校母毎気水火父生男白百目社空立耳聞花行西見言話語読買足車週道金長間雨電食飲駅高魚"
            .toCharArray()
            .map { it.toString() }

//    val list = kanjiDataStore.getKanjiList()

    val list = n5

    LazyColumn {

        items(list) {

            Row {

                val strokes = kanjiDataStore.getStrokes(kanji = it)
                val commands = strokes.map { SvgCommandParser.parse(it) }
                val paths = commands.map { SvgPathCreator.convert(it) }

                Button(
                    onClick = { onKanjiClicked.invoke(it) }
                ) {

                    Kanji(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Green),
                        strokes = paths
                    )

                }

                Column {

//                    SelectionContainer {
//                        Text(
//                            text = it.toString(),
//                            fontSize = 22.sp
//                        )
//                    }

                }


            }

        }


    }


}