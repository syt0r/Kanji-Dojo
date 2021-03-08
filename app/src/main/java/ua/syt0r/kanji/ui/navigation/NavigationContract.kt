package ua.syt0r.kanji.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf

interface NavigationContract {

    interface Navigator {
        fun navigate(route: String)
    }

}

val LocalNavigator = staticCompositionLocalOf<NavigationContract.Navigator> {
    error("Navigator does not exists")
}