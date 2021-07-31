package ua.syt0r.kanji.presentation.screen.screen.home.screen.search

interface SearchScreenContract {

    interface ViewModel {
        fun searchKanji(kanji: String): List<String>
    }

}