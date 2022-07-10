package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionOption
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeMode
import javax.inject.Inject
import javax.inject.Singleton

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

@Singleton
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserDataContract.PreferencesRepository {

    private val analyticsEnabledKey = booleanPreferencesKey("analytics_enabled")

    private val practiceModeKey = stringPreferencesKey("practice_mode")
    private val shuffleKey = booleanPreferencesKey("shuffle")
    private val selectionOptionKey = stringPreferencesKey("selection_option")
    private val firstItemsTextKey = stringPreferencesKey("first_items_text")

    @Inject
    constructor(@ApplicationContext context: Context) : this(
        dataStore = context.preferencesDataStore
    )

    override val analyticsEnabled: Flow<Boolean>
        get() = dataStore.data
            .map {
                Logger.d("analyticsEnabled update")
                it[analyticsEnabledKey] ?: true
            }

    override suspend fun setAnalyticsEnabled(enabled: Boolean) {
        dataStore.edit {
            Logger.d("analyticsEnabled edit")
            it[analyticsEnabledKey] = true
        }
    }

    override suspend fun getSelectionConfiguration(): SelectionConfiguration? {
        return dataStore.data.first().let {
            val practiceModeName = it[practiceModeKey]
            val shuffle = it[shuffleKey]
            val selectionOptionName = it[selectionOptionKey]
            val firstItemsText = it[firstItemsTextKey]
            if (practiceModeName != null && shuffle != null && selectionOptionName != null && firstItemsText != null) {
                SelectionConfiguration(
                    practiceMode = WritingPracticeMode.valueOf(practiceModeName),
                    shuffle = shuffle,
                    option = SelectionOption.valueOf(selectionOptionName),
                    firstItemsText = firstItemsText
                )
            } else {
                null
            }
        }
    }

    override suspend fun setSelectionConfiguration(configuration: SelectionConfiguration) {
        dataStore.edit {
            it[practiceModeKey] = (configuration.practiceMode as WritingPracticeMode).name
            it[shuffleKey] = configuration.shuffle
            it[selectionOptionKey] = configuration.option.name
            it[firstItemsTextKey] = configuration.firstItemsText
        }
    }


}