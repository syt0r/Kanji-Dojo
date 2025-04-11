package ua.syt0r.kanji.core.file

import io.ktor.utils.io.ByteReadChannel

expect class PlatformFile

interface PlatformFileHandler {
    fun read(file: PlatformFile): ByteReadChannel
    suspend fun write(file: PlatformFile, channel: ByteReadChannel)
    fun delete(file: PlatformFile)
}