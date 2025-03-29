package ua.syt0r.kanji.core.sync.use_case

import io.ktor.utils.io.readByteArray
import io.ktor.utils.io.readInt
import io.ktor.utils.io.readShort
import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.ApiSyncDataInfo
import ua.syt0r.kanji.core.NetworkApi
import ua.syt0r.kanji.core.backup.BackupManager
import ua.syt0r.kanji.core.file.PlatformFileHandler
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.sync.SyncBackupFileProvider
import ua.syt0r.kanji.core.toPreferencesType
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract
import ua.syt0r.kanji.presentation.common.json

interface ApplyRemoteSyncDataUseCase {
    suspend operator fun invoke(): ApplySyncResult
}

sealed interface ApplySyncResult {
    data object Success : ApplySyncResult
    data class Fail(val issue: ApiRequestIssue) : ApplySyncResult
}

class DefaultApplyRemoteSyncDataUseCase(
    private val networkApi: NetworkApi,
    private val appPreferences: PreferencesContract.AppPreferences,
    private val syncBackupFileProvider: SyncBackupFileProvider,
    private val platformFileHandler: PlatformFileHandler,
    private val backupManager: BackupManager
) : ApplyRemoteSyncDataUseCase {

    override suspend fun invoke(): ApplySyncResult {
        Logger.logMethod()

        val syncBackupFile = syncBackupFileProvider()

        val result = networkApi.getSyncData().mapCatching {
            val channel = it.value
            appPreferences.subscriptionAlert.set(it.alert)

            val jsonLength = channel.readInt()
            Logger.d("jsonLength[$jsonLength]")
            val jsonLengthBytes = channel.readShort().toInt()
            Logger.d("jsonLengthBytes[$jsonLengthBytes]")

            val infoJson = channel.readByteArray(jsonLengthBytes).decodeToString()
            Logger.d("infoJson[$infoJson]")

            val syncDataInfo = json.decodeFromString<ApiSyncDataInfo>(infoJson)
                .toPreferencesType()

            platformFileHandler.write(syncBackupFile, channel)

            backupManager.restoreFrom(syncBackupFile)

            appPreferences.lastSyncedDataInfo.set(syncDataInfo)

            ApplySyncResult.Success
        }.getOrElse {
            ApplySyncResult.Fail(ApiRequestIssue.classify(it))
        }

        platformFileHandler.delete(syncBackupFile)

        return result
    }

}