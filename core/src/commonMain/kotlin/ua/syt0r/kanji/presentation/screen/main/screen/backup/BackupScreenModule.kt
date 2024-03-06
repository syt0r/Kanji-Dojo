package ua.syt0r.kanji.presentation.screen.main.screen.backup

import org.koin.dsl.module

val backupScreenModule = module {

    factory<BackupContract.ViewModel> {
        BackupViewModel(
            viewModelScope = it.component1(),
            backupManager = get()
        )
    }

}