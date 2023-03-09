package ua.syt0r.kanji.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreenContract

/***
 * Wraps screen's view model interface with android's view model to survive configuration changes
 * Allows to use viewModel's coroutine scope
 */
class AndroidViewModelWrapper<T>(
    provider: (coroutineScope: CoroutineScope) -> T
) : ViewModel() {
    val viewModel = provider(viewModelScope)
}

@Composable
inline fun <reified T> getMultiplatformViewMode(): T {
    return getViewModel<AndroidViewModelWrapper<T>>(
        qualifier = named<T>()
    ).viewModel
}

inline fun <reified T> Module.multiplatformViewModel() {
    viewModel<AndroidViewModelWrapper<T>>(
        qualifier = named<T>()
    ) {
        AndroidViewModelWrapper { coroutineScope -> get { parametersOf(coroutineScope) } }
    }
}

val androidViewModelModule = module {
    multiplatformViewModel<HomeScreenContract.ViewModel>()
}