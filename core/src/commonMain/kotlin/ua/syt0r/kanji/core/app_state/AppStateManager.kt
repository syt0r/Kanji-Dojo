package ua.syt0r.kanji.core.app_state

import kotlinx.coroutines.flow.StateFlow

interface AppStateManager {
    val appStateFlow: StateFlow<LoadableData<AppState>>
    fun invalidate()
}
