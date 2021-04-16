package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract.State
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.WritingDashboardScreenCategory
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.WritingDashboardScreenData
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.WritingDashboardScreenItem
import ua.syt0r.svg.SvgCommandParser


class WritingDashboardViewModel(
    private val kanjiDataStore: KanjiDataStoreContract.DataStore
) : ViewModel(), WritingDashboardScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchData()
        }
    }

    private fun fetchData() {

        val previewKanji = kanjiDataStore.getStrokes("あ")
            .map { SvgCommandParser.parse(it) }
            .map { SvgPathCreator.convert(it) }

        val basicCategory = WritingDashboardScreenCategory(
            title = "Basic",
            items = listOf(
                WritingDashboardScreenItem.SingleItem(
                    title = "Hiragana",
                    previewKanji = previewKanji
                ),
                WritingDashboardScreenItem.SingleItem(
                    title = "Katakana",
                    previewKanji = previewKanji
                )
            )
        )

        val predefinedCategory = WritingDashboardScreenCategory(
            title = "Predefined",
            items = listOf(
                WritingDashboardScreenItem.ItemGroup(
                    "JLPT",
                    previewKanji = previewKanji,
                    items = (5 downTo 1).map {
                        WritingDashboardScreenItem.SingleItem(
                            title = "N$it",
                            previewKanji = previewKanji
                        )
                    }
                )
            )
        )

        state.postValue(
            State.Loaded(
                WritingDashboardScreenData(
                    categories = listOf(basicCategory, predefinedCategory)
                )
            )
        )

    }

}