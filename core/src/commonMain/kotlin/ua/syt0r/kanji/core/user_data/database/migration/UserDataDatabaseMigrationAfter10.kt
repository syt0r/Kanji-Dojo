package ua.syt0r.kanji.core.user_data.database.migration

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.flow.first
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.DetailedJapaneseWord
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.suspended_property.EnumSuspendedPropertyType.Companion.enumSuspendedPropertyType
import ua.syt0r.kanji.core.user_data.database.DatabaseMigrationState
import ua.syt0r.kanji.core.user_data.database.DatabaseMigrationState.Running.Progress
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.database.updateState
import kotlin.time.measureTime

class UserDataDatabaseMigrationAfter10(
    private val preferences: DataStore<Preferences>,
    private val appDataRepository: AppDataRepository,
    private val migrationObservable: UserDataDatabaseContract.MigrationObservable
) : UserDataDatabaseContract.Migration {

    override val version: Long = 10

    private data class LegacyVocabDeckEntry(
        val wordId: Long,
        val deckId: Long
    )

    private data class NewDeckEntryData(
        val wordId: Long,
        val deckId: Long,
        val kanjiReading: String?,
        val kanaReading: String
    )

    private data class MigratedEntryData(
        val wordId: Long,
        val deckId: Long,
        val cardId: Long
    )

    private enum class PreferencesVocabReadingPriority { Default, Kanji, Kana }

    private val readingPriorityKey = "vocab_reading_priority"

    override suspend fun execute(driver: SqlDriver) {
        migrationObservable.updateState("Loading data...")

        val priorityPropertyType = enumSuspendedPropertyType<PreferencesVocabReadingPriority>()
        val value = preferences.data.first()[stringPreferencesKey(readingPriorityKey)]
        val readingPriority = value?.let { priorityPropertyType.convertToExposed(it) }
            ?: PreferencesVocabReadingPriority.Default

        driver.executeQuery(
            identifier = null,
            sql = "PRAGMA busy_timeout = 300000;",
            mapper = { QueryResult.Unit },
            parameters = 0
        )

        val legacyEntries = driver.executeQuery(
            identifier = null,
            sql = "SELECT word_id, deck_id FROM vocab_deck_entry_old;",
            mapper = {
                val list = mutableListOf<LegacyVocabDeckEntry>()
                while (it.next().value) {
                    list.add(
                        LegacyVocabDeckEntry(
                            wordId = it.getLong(0)!!,
                            deckId = it.getLong(1)!!
                        )
                    )
                }
                QueryResult.Value(list)
            },
            parameters = 0
        ).value

        val legacyWordIdList = legacyEntries.map { it.wordId }.distinct()

        val wordsToReading = legacyWordIdList
            .mapIndexedNotNull { index, wordId ->
                migrationObservable.updateState(
                    message = "Updating vocab cards...",
                    progress = Progress(index, legacyWordIdList.size)
                )

                Logger.d("Migrating vocab card #$index, word[$wordId]")

                val word: DetailedJapaneseWord
                val time = measureTime { word = appDataRepository.getDetailedWord(wordId) }
                val readings = word.senseList.flatMap { it.readings }

                Logger.d("Loaded word info for [$wordId], readings[${readings.size}], time[$time]")

                val targetReading = readings
                    .firstOrNull {
                        when (readingPriority) {
                            PreferencesVocabReadingPriority.Default -> true
                            PreferencesVocabReadingPriority.Kanji -> it.kanji != null
                            PreferencesVocabReadingPriority.Kana -> it.kanji == null
                        }
                    }
                    ?: readings.firstOrNull()
                    ?: return@mapIndexedNotNull null

                wordId to targetReading
            }
            .toMap()


        val vocabCardsToDeckId = legacyEntries.mapNotNull { (wordId, deckId) ->
            val targetReading = wordsToReading[wordId] ?: return@mapNotNull null

            NewDeckEntryData(
                kanjiReading = targetReading.kanji,
                kanaReading = targetReading.kana,
                wordId = wordId,
                deckId = deckId
            )
        }

        migrationObservable.updateState("Applying changes...")

        val migratedEntries = vocabCardsToDeckId.map { data ->
            driver.execute(
                identifier = null,
                sql = """
                INSERT OR IGNORE INTO vocab_deck_entry(deck_id, kanji_reading, kana_reading, word_id)
                VALUES (?, ?, ?, ?);
                """.trimIndent(),
                parameters = 4
            ) {
                bindLong(0, data.deckId)
                bindString(1, data.kanjiReading)
                bindString(2, data.kanaReading)
                bindLong(3, data.wordId)
            }

            val cardId = driver.executeQuery(
                identifier = null,
                sql = "SELECT last_insert_rowid();",
                mapper = {
                    it.next()
                    QueryResult.Value(it.getLong(0))
                },
                parameters = 0
            ).value!!
            MigratedEntryData(data.wordId, data.deckId, cardId)
        }

        migratedEntries.forEach {
            driver.execute(
                identifier = null,
                sql = "UPDATE OR REPLACE fsrs_card SET key = ? WHERE key = ?;",
                parameters = 2
            ) {
                bindString(0, it.cardId.toString())
                bindString(1, it.wordId.toString())
            }

            driver.execute(
                identifier = null,
                sql = "UPDATE OR REPLACE review_history SET key = ? WHERE key = ?;",
                parameters = 2
            ) {
                bindString(0, it.cardId.toString())
                bindString(1, it.wordId.toString())
            }
        }

        driver.execute(
            identifier = null,
            sql = "DROP TABLE vocab_deck_entry_old;",
            parameters = 0
        )

        migrationObservable.updateState(DatabaseMigrationState.Idle)
    }

}