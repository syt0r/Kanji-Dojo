package ua.syt0r.kanji.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.androidx.viewmodel.ViewModelOwner.Companion.from
import org.koin.core.qualifier.Qualifier

//TODO remove when koin will include support of latest compose version

@Composable
inline fun <reified T : ViewModel> getViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val owner = LocalViewModelStoreOwner.current!!.viewModelStore
    return remember {
        GlobalContext.get().getViewModel(qualifier, owner = { from(owner) }, parameters = parameters)
    }
}

@Composable
inline fun <reified T> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T = remember {
    GlobalContext.get().get(qualifier, parameters)
}