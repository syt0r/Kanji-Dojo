package ua.syt0r.kanji.di

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import ua.syt0r.kanji.core.backup.PlatformFile
import ua.syt0r.kanji.core.backup.PlatformFileHandler

class IosPlatformFileHandler : PlatformFileHandler {
    override fun getInputStream(file: PlatformFile): ByteReadChannel {
        TODO("Not yet implemented")
    }

    override fun getOutputStream(file: PlatformFile): ByteWriteChannel {
        TODO("Not yet implemented")
    }

}
