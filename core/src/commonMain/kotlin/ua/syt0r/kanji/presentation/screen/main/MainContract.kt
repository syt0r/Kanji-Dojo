package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.State

interface MainContract {

    interface ViewModel {
        val shouldShowVersionChangeDialog: State<Boolean>
        fun markVersionChangeDialogShown()
    }

}