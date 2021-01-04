package ua.syt0r.kanji.screen

sealed class Navigation {

    abstract val routeName: String

    object Home : Navigation() {
        override val routeName = "home"
    }

    object KanjiTest : Navigation() {

        override val routeName = "kanji_test"
        const val KANJI_INDEX_ARGUMENT_KEY = "kanji"

        fun createRoute(kanji: String): String = "$routeName/$kanji"

    }

}