package ua.syt0r.kanji.ui.screen.screen.kanji_info

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.di.get

@Composable
fun KanjiInfoScreen(
    kanji: String,
    dataStore: KanjiDataStoreContract.DataStore = get()
) {

    val data = dataStore.getKanji(kanji)

    Text(text = data.toString())

}