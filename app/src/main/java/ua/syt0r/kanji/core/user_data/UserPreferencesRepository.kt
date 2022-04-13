package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserDataContract.PreferencesRepository {

    @Inject
    constructor(context: Context) : this(
        dataStore = context.preferencesDataStore
    )

}