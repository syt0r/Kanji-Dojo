package ua.syt0r.kanji.core.backup

import android.content.Context
import android.net.Uri
import ua.syt0r.kanji.core.user_data.UserDataDatabaseManager
import java.io.OutputStream

data class PlatformFileAndroid(
    val fileUri: Uri
) : PlatformFile

class BackupManagerAndroid(
    userDataDatabaseManager: UserDataDatabaseManager,
    private val context: Context
) : BaseBackupManager(userDataDatabaseManager) {

    override fun getOutputStream(platformFile: PlatformFile): OutputStream {
        platformFile as PlatformFileAndroid
        return context.contentResolver.openOutputStream(platformFile.fileUri)!!
    }

}