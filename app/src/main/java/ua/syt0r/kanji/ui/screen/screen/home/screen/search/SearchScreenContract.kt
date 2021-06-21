package ua.syt0r.kanji.ui.screen.screen.home.screen.search

interface SearchScreenContract {

    interface ViewModel {
        fun searchKanji(kanji: String): List<String>
    }

}