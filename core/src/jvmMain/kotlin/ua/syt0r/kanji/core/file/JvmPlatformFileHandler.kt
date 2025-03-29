package ua.syt0r.kanji.core.file

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.streams.asByteWriteChannel

class JvmPlatformFileHandler : PlatformFileHandler {

    override fun read(file: PlatformFile): ByteReadChannel {
        return file.javaFile.inputStream().toByteReadChannel()
    }

    override suspend fun write(file: PlatformFile, channel: ByteReadChannel) {
        file.javaFile.outputStream().use {
            val writeChannel = it.asByteWriteChannel()
            channel.copyTo(writeChannel)
            writeChannel.flushAndClose()
        }
    }

    override fun delete(file: PlatformFile) {
        file.javaFile.delete()
    }

}