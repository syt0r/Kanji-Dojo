package ua.syt0r.kanji.presentation.screen.main.screen.about

interface AboutScreenContract {

    interface ViewModel {
        fun reportScreenShown()
        fun reportUrlClick(url: String)
    }

}