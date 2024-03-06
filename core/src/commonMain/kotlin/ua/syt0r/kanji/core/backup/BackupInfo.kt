package ua.syt0r.kanji.core.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupInfo(
    val databaseVersion: Long,
    val backupCreateTimestamp: Long,
    val userDatabaseFileName: String
)