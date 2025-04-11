package ua.syt0r.kanji.core.file

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem

class IosPlatformFileHandler : PlatformFileHandler {

    override fun read(file: PlatformFile): ByteReadChannel {
        return ByteReadChannel(SystemFileSystem.source(file.path).buffered())
    }

    override suspend fun write(
        file: PlatformFile,
        channel: ByteReadChannel
    ) {
        SystemFileSystem.sink(file.path, append = false).buffered().apply {
            transferFrom(channel.asSource())
            flush()
            close()
        }
    }

    override fun delete(file: PlatformFile) {
        SystemFileSystem.delete(file.path)
    }

}