package ua.syt0r.kanji.core.user_data.database.migration

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.flow.first
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.suspended_property.EnumSuspendedPropertyType.Companion.enumSuspendedPropertyType
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract

class UserDataDatabaseMigrationAfter9(
    private val preferences: DataStore<Preferences>,
    private val appDataRepository: AppDataRepository
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
        val priorityPropertyType = enumSuspendedPropertyType<PreferencesVocabReadingPriority>()
        val value = preferences.data.first()[stringPreferencesKey(readingPriorityKey)]
        val readingPriority = value?.let { priorityPropertyType.convertToExposed(it) }
            ?: PreferencesVocabReadingPriority.Default

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

        val wordsToReading = legacyEntries.map { it.wordId }
            .distinct()
            .associateWith { wordId ->
                val word = appDataRepository.getDetailedWord(wordId)
                val targetReading = word.senseList.asSequence()
                    .flatMap { it.readings }
                    .firstOrNull {
                        when (readingPriority) {
                            PreferencesVocabReadingPriority.Default -> true
                            PreferencesVocabReadingPriority.Kanji -> it.kanji != null
                            PreferencesVocabReadingPriority.Kana -> it.kanji == null
                        }
                    }
                    ?: word.senseList
                        .first()
                        .readings
                        .first()

                targetReading
            }

        val vocabCardsToDeckId = legacyEntries.map { (wordId, deckId) ->
            val targetReading = wordsToReading.getValue(wordId)

            NewDeckEntryData(
                kanjiReading = targetReading.kanji,
                kanaReading = targetReading.kana,
                wordId = wordId,
                deckId = deckId
            )
        }

        val migratedEntries = vocabCardsToDeckId.map { data ->
            val cardId = driver.executeQuery(
                identifier = null,
                sql = """
                    INSERT OR IGNORE INTO vocab_deck_entry(deck_id, kanji_reading, kana_reading, word_id)
                    VALUES (?, ?, ?, ?);
                    SELECT last_insert_rowid();
                """.trimIndent(),
                mapper = {
                    it.next()
                    QueryResult.Value(it.getLong(0))
                },
                parameters = 4,
                binders = {
                    bindLong(0, data.deckId)
                    bindString(1, data.kanjiReading)
                    bindString(2, data.kanaReading)
                    bindLong(3, data.wordId)
                }
            ).value!!
            MigratedEntryData(data.wordId, data.deckId, cardId)
        }

        migratedEntries.forEach {
            driver.execute(
                null,
                "UPDATE fsrs_card SET key = ? WHERE key = ?;",
                2
            ) {
                bindLong(0, it.cardId)
                bindLong(1, it.wordId)
            }

            driver.execute(
                null,
                "UPDATE review_history SET key = ? WHERE key = ?;",
                2
            ) {
                bindLong(0, it.cardId)
                bindLong(1, it.wordId)
            }
        }

        driver.execute(null, "DROP TABLE vocab_deck_entry_old;", 0)
    }

}