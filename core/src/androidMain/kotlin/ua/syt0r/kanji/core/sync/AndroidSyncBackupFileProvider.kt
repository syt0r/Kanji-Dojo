package ua.syt0r.kanji.core.sync

import android.content.Context
import androidx.core.net.toUri
import ua.syt0r.kanji.core.file.PlatformFile
import java.io.File

class AndroidSyncBackupFileProvider(
    private val context: Context
) : SyncBackupFileProvider {

    override fun invoke(): PlatformFile = PlatformFile(
        File(context.cacheDir, "sync_backup.zip").toUri()
    )

}