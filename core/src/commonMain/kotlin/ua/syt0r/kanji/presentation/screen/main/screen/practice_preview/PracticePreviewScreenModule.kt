package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case.*

val practicePreviewScreenModule = module {

    factory<PracticePreviewScreenContract.ReloadDataUseCase> {
        PracticePreviewReloadStateUseCase(
            userPreferencesRepository = get(),
            practiceRepository = get(),
            fetchGroupItemsUseCase = get(),
            filterGroupItemsUseCase = get(),
            sortGroupItemsUseCase = get(),
            createGroupsUseCase = get()
        )
    }

    factory<PracticePreviewScreenContract.FetchGroupItemsUseCase> {
        PracticePreviewFetchGroupItemsUseCase(
            practiceRepository = get(),
            kanjiDataRepository = get(),
            getPracticeSummaryUseCase = get()
        )
    }

    factory<PracticePreviewScreenContract.FilterGroupItemsUseCase> {
        PracticePreviewFilterGroupItemsUseCase()
    }

    factory<PracticePreviewScreenContract.SortGroupItemsUseCase> {
        PracticePreviewSortGroupItemsUseCase()
    }

    factory<PracticePreviewScreenContract.CreatePracticeGroupsUseCase> {
        CreatePracticeGroupsUseCase()
    }

    factory<PracticePreviewScreenContract.GetPracticeSummary> {
        PracticePreviewCharacterReviewSummary(
            practiceRepository = get(),
            timeUtils = get()
        )
    }

    factory<PracticePreviewScreenContract.ViewModel> {
        PracticePreviewViewModel(
            viewModelScope = it.component1(),
            reloadDataUseCase = get(),
            userPreferencesRepository = get(),
            filterGroupItemsUseCase = get(),
            sortGroupItemsUseCase = get(),
            createGroupsUseCase = get(),
            analyticsManager = get()
        )
    }

}