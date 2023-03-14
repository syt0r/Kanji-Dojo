package ua.syt0r.kanji.core.kanji_data

import kotlinx.coroutines.Deferred
import ua.syt0r.kanji.core.kanji_data.db.KanjiDatabase

interface KanjiDatabaseProvider {
    // TODO versioning or hash check to avoid recreating
    fun provideAsync(): Deferred<KanjiDatabase>
}