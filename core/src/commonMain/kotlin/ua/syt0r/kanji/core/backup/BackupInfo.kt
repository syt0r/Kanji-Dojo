package ua.syt0r.kanji.core.backup

data class BackupInfo(
    val databaseVersion: String,
    val lastUpdateTimestamp: Long
)