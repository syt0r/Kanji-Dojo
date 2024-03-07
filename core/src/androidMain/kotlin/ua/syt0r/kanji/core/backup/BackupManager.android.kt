package ua.syt0r.kanji.core.backup

import android.content.ContentResolver
import android.net.Uri
import ua.syt0r.kanji.core.user_data.UserDataDatabaseManager
import java.io.InputStream
import java.io.OutputStream

data class PlatformFileAndroid(
    val fileUri: Uri
) : PlatformFile

class BackupManagerAndroid(
    userDataDatabaseManager: UserDataDatabaseManager,
    private val contentResolver: ContentResolver
) : BaseBackupManager(userDataDatabaseManager) {

    override fun getInputStream(platformFile: PlatformFile): InputStream {
        platformFile as PlatformFileAndroid
        return contentResolver.openInputStream(platformFile.fileUri)!!
    }

    override fun getOutputStream(platformFile: PlatformFile): OutputStream {
        platformFile as PlatformFileAndroid
        return contentResolver.openOutputStream(platformFile.fileUri)!!
    }

}