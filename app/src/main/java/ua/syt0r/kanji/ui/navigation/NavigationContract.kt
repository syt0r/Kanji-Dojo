package ua.syt0r.kanji.ui.navigation

import androidx.compose.runtime.ambientOf

interface NavigationContract {

    companion object {
        val navigator = ambientOf<Navigator> { error("Navigator does not exists") }
    }

    interface Navigator {
        fun navigate(route: String)
    }

}