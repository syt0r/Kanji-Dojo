package ua.syt0r.kanji.screen

object Navigation {

    sealed class Destination {

        abstract val routeName: String

        object Home : Destination() {
            override val routeName = "home"
        }

        object KanjiTest : Destination() {
            override val routeName = "kanji_test"

        }


    }

}