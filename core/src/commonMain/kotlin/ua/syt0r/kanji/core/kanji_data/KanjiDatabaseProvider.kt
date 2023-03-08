package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.db.KanjiDatabase

interface KanjiDatabaseProvider {
    // TODO versioning or hash check to avoid recreating
    // TODO make async + update repository
    fun provide(): KanjiDatabase
}