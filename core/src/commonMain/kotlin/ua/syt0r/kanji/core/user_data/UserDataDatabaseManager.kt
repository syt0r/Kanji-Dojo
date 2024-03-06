package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.userdata.db.PracticeQueries

interface UserDataDatabaseManager {

    val dataChangedFlow: SharedFlow<Unit>

    suspend fun <T> runTransaction(
        notifyDataChange: Boolean = false,
        block: PracticeQueries.() -> T
    ): T

}

abstract class BaseUserDataDatabaseManager(
    coroutineScope: CoroutineScope
) : UserDataDatabaseManager {

    protected data class DatabaseInstanceData(
        val sqlDriver: SqlDriver,
        val database: UserDataDatabase
    )

    private val databaseInstance = MutableStateFlow<DatabaseInstanceData?>(null)
    private val onDataUpdatedFlow = MutableSharedFlow<Unit>()

    override val dataChangedFlow: SharedFlow<Unit> = onDataUpdatedFlow

    init {
        coroutineScope.launch(Dispatchers.IO) {
            databaseInstance.value = getDatabase()
        }
    }

    protected fun getMigrationCallbacks(): Array<AfterVersion> = arrayOf(
        AfterVersion(3) { UserDataDatabaseMigrationAfter3.handleMigrations(it) },
        AfterVersion(4) { UserDataDatabaseMigrationAfter4.handleMigrations(it) }
    )

    protected abstract suspend fun getDatabase(): DatabaseInstanceData

    override suspend fun <T> runTransaction(
        notifyDataChange: Boolean,
        block: PracticeQueries.() -> T
    ): T {
        val result = withContext(Dispatchers.IO) {
            val queries = databaseInstance.filterNotNull()
                .first()
                .database
                .practiceQueries
            queries.transactionWithResult { queries.block() }
        }

        if (notifyDataChange) onDataUpdatedFlow.emit(Unit)

        return result
    }

}
