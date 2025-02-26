package ua.syt0r.kanji.core.user_data.database

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DefaultMigrationObservable : UserDataDatabaseContract.MigrationObservable {

    private val _state = MutableStateFlow<DatabaseMigrationState>(DatabaseMigrationState.Idle)
    override val state: StateFlow<DatabaseMigrationState> = _state

    override fun updateState(updatedState: DatabaseMigrationState) {
        _state.value = updatedState
    }

}

fun UserDataDatabaseContract.MigrationObservable.updateState(
    message: String,
    progress: DatabaseMigrationState.Running.Progress? = null
) = updateState(
    DatabaseMigrationState.Running(message, progress)
)
