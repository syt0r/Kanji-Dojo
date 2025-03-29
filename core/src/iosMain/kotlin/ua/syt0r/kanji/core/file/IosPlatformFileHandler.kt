package ua.syt0r.kanji.core.file

import io.ktor.utils.io.ByteReadChannel

class IosPlatformFileHandler : PlatformFileHandler {
    override fun read(file: PlatformFile): ByteReadChannel {
        TODO("Not yet implemented")
    }

    override suspend fun write(
        file: PlatformFile,
        channel: ByteReadChannel
    ) {
        TODO("Not yet implemented")
    }

    override fun delete(file: PlatformFile) {
        TODO("Not yet implemented")
    }
}