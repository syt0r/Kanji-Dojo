package  ua.syt0r.kanji.ui.screen.screen.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel(), HomeScreenContract.ViewModel {

    override val currentScreen = MutableLiveData(HomeScreenContract.Screen.DEFAULT)

    override fun selectScreen(screen: HomeScreenContract.Screen) {
        currentScreen.value = screen
    }

}