package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.Deferred
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase

interface UserDataDatabaseProvider {
    fun provideAsync(): Deferred<UserDataDatabase>
}