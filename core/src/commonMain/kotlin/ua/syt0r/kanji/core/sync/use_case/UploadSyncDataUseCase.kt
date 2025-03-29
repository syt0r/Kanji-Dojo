package ua.syt0r.kanji.core.sync.use_case

import io.ktor.client.request.forms.ChannelProvider
import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.NetworkApi
import ua.syt0r.kanji.core.backup.BackupManager
import ua.syt0r.kanji.core.file.PlatformFileHandler
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.sync.SyncBackupFileProvider
import ua.syt0r.kanji.core.toApiType
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract

interface UploadSyncDataUseCase {
    suspend operator fun invoke(): UploadSyncDataResult
}

sealed interface UploadSyncDataResult {
    object Success : UploadSyncDataResult
    data class Fail(val issue: ApiRequestIssue) : UploadSyncDataResult
}

class DefaultUploadSyncDataUseCase(
    private val getLocalSyncDataInfoUseCase: GetLocalSyncDataInfoUseCase,
    private val appPreferences: PreferencesContract.AppPreferences,
    private val networkApi: NetworkApi,
    private val syncBackupFileProvider: SyncBackupFileProvider,
    private val platformFileHandler: PlatformFileHandler,
    private val backupManager: BackupManager
) : UploadSyncDataUseCase {

    override suspend fun invoke(): UploadSyncDataResult {
        Logger.logMethod()
        val backupFile = syncBackupFileProvider()
        return runCatching {
            val localSyncDataInfo = getLocalSyncDataInfoUseCase()
            backupManager.backupTo(backupFile)
            networkApi.updateSyncData(
                syncDataInfo = localSyncDataInfo.toApiType(),
                channelProvider = ChannelProvider { platformFileHandler.read(backupFile) }
            )
                .mapCatching { appPreferences.subscriptionAlert.set(it.alert) }
                .getOrThrow()

            platformFileHandler.delete(backupFile)

            appPreferences.lastSyncedDataInfo.set(localSyncDataInfo)

            UploadSyncDataResult.Success
        }.getOrElse {
            platformFileHandler.delete(backupFile)
            UploadSyncDataResult.Fail(issue = ApiRequestIssue.classify(it))
        }
    }

}
