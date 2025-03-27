package ua.syt0r.kanji.core

import io.ktor.client.request.forms.ChannelProvider
import io.ktor.utils.io.ByteWriteChannel
import ua.syt0r.kanji.core.backup.PlatformFile
import ua.syt0r.kanji.core.sync.SyncBackupFileManager

class IosSyncBackupFileManager : SyncBackupFileManager {
    override fun getFile(): PlatformFile {
        TODO("Not yet implemented")
    }

    override fun getChannelProvider(): ChannelProvider {
        TODO("Not yet implemented")
    }

    override fun outputStream(): ByteWriteChannel {
        TODO("Not yet implemented")
    }

    override fun clean() {
        TODO("Not yet implemented")
    }

}
