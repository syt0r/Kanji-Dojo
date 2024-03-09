package ua.syt0r.kanji.core.backup

import java.io.File
import java.io.InputStream
import java.io.OutputStream

data class PlatformFileJvm(val file: File) : PlatformFile

class JvmPlatformFileHandler : PlatformFileHandler {

    override fun getInputStream(file: PlatformFile): InputStream {
        file as PlatformFileJvm
        return file.file.inputStream()
    }

    override fun getOutputStream(file: PlatformFile): OutputStream {
        file as PlatformFileJvm
        return file.file.outputStream()
    }

}
