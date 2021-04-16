package ua.syt0r.kanji.ui.screen.screen.home.screen.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.di.get
import ua.syt0r.kanji.ui.common.kanji.Kanji
import ua.syt0r.kanji.ui.common.kanji.KanjiBackground
import ua.syt0r.svg.SvgCommandParser

@Composable
fun SearchScreen(
    store: KanjiDataStoreContract.DataStore = get()
) {

    Column {

        val kanjiState = remember { mutableStateOf("") }
        val textState = remember { mutableStateOf("") }

        Row {

            TextField(
                value = textState.value,
                onValueChange = { textState.value = it }
            )

            Button(
                onClick = { kanjiState.value = textState.value }
            ) {
                Text(text = "Search")
            }


        }

        if (kanjiState.value.isNotEmpty()) {

            Logger.d("drawing kanji")

            Spacer(modifier = Modifier.height(24.dp))

            KanjiBackground(
                Modifier.size(200.dp)
            ) {

                Kanji(
                    strokes = store.getStrokes(kanjiState.value)
                        .map { SvgCommandParser.parse(it) }
                        .map { SvgPathCreator.convert(it) },
                    modifier = Modifier.size(200.dp)
                )

            }


        }

    }


}