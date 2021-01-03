package ua.syt0r.kanji.screen.main.sub_screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.selection.SelectionContainer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.screen.common.Kanji
import ua.syt0r.kanji.screen.main.DataSource
import ua.syt0r.svg_parser.SvgCommandParser

@Composable
fun HomeScreen(
    navigateToKanjiTest: (Int) -> Unit
) {
    Scaffold(
        bottomBar = { BottomBar() }
    ) {
        ScreenContent(
            onKanjiClicked = { kanjiIndex -> navigateToKanjiTest(kanjiIndex) }
        )
    }
}

@Composable
private fun BottomBar() {
    BottomAppBar(
        modifier = Modifier.background(Color.Blue)
    ) {

    }
}

@Composable
private fun ScreenContent(
    onKanjiClicked: (Int) -> Unit
) {
    val list = DataSource.data.mapIndexed { index, kanji ->
        index to kanji
    }

    LazyColumn {

        items(list) {

            Row {

                val commands = it.second.strokes.map { SvgCommandParser.parse(it) }
                val strokes = commands.map { SvgPathCreator.convert(it) }

                Button(
                    onClick = { onKanjiClicked.invoke(it.first) }
                ) {

                    Kanji(
                        modifier = Modifier.size(200.dp)
                            .background(Color.Green),
                        strokes = strokes
                    )

                }

                Column {

                    SelectionContainer {
                        Text(
                            text = it.second.char.toString(),
                            fontSize = 22.sp
                        )
                    }

                    Text(text = commands.flatten()
                        .map { it::class.java.simpleName }
                        .distinct()
                        .joinToString("\n"))
                }


            }

        }


    }


}