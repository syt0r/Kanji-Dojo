package ua.syt0r.kanji.ui.screen

interface MainContract {

    interface Navigation {
        fun navigateToHome()
        fun navigateToPracticeSet()
        fun navigateToWritingPractice(kanjiList: List<String>)
        fun navigateToAbout()
        fun navigateToKanjiInfo(kanji: String)
    }

}