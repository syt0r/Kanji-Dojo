package ua.syt0r.kanji.core.backup

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import java.io.File

data class PlatformFileJvm(val file: File) : PlatformFile

class JvmPlatformFileHandler : PlatformFileHandler {

    override fun getInputStream(file: PlatformFile): ByteReadChannel {
        file as PlatformFileJvm
        return file.file.inputStream().toByteReadChannel()
    }

    override fun getOutputStream(file: PlatformFile): ByteWriteChannel {
        TODO()
//        file as PlatformFileJvm
//        return file.file.outputStream()
    }

}
