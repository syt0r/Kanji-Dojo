package ua.syt0r.kanji.core.sync.use_case

import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.NetworkApi
import ua.syt0r.kanji.core.backup.BackupManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.sync.SyncBackupFileManager
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract

interface ApplyRemoteSyncDataUseCase {
    suspend operator fun invoke(): ApplySyncResult
}

sealed interface ApplySyncResult {
    data object Success : ApplySyncResult
    data class Fail(val issue: ApiRequestIssue) : ApplySyncResult
}

class DefaultApplyRemoteSyncDataUseCase(
    private val syncBackupFileManager: SyncBackupFileManager,
    private val backupManager: BackupManager,
    private val appPreferences: PreferencesContract.AppPreferences,
    private val networkApi: NetworkApi
) : ApplyRemoteSyncDataUseCase {

    override suspend fun invoke(): ApplySyncResult {
        Logger.logMethod()

        val result = networkApi.getSyncData().mapCatching {
            val byteReadChannel = it.value
            appPreferences.subscriptionAlert.set(it.alert)
            TODO("ios")

//            val inputStream = byteReadChannel.toInputStream()
//            val dataInputStream = DataInputStream(inputStream)
//
//            val infoLength = dataInputStream.readInt()
//            val infoJson = dataInputStream.readUTF()
//            Logger.d("infoLength[$infoLength] infoJson[$infoJson]")
//
//            val syncDataInfo = json.decodeFromString<ApiSyncDataInfo>(infoJson)
//                .toPreferencesType()
//
//            inputStream.transferToCompat(syncBackupFileManager.outputStream())
//
//            backupManager.restore(syncBackupFileManager.getFile())
//
//            appPreferences.lastSyncedDataInfo.set(syncDataInfo)

            ApplySyncResult.Success
        }.getOrElse {
            ApplySyncResult.Fail(ApiRequestIssue.classify(it))
        }

        syncBackupFileManager.clean()

        return result
    }

}