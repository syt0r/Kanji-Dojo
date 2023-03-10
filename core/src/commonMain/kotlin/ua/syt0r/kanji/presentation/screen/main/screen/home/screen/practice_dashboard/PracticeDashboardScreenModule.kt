package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case.PracticeDashboardLoadDataUseCase

val practiceDashboardScreenModule = module {

    factory<PracticeDashboardScreenContract.LoadDataUseCase> {
        PracticeDashboardLoadDataUseCase(get())
    }

    factory<PracticeDashboardScreenContract.ViewModel> {
        PracticeDashboardViewModel(
            viewModelScope = it.component1(),
            loadDataUseCase = get(),
            userPreferencesRepository = get(),
            analyticsManager = get()
        )
    }

}