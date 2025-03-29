package ua.syt0r.kanji.core.file

import android.content.ContentResolver
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.streams.asByteWriteChannel

class AndroidPlatformFileHandler(
    private val contentResolver: ContentResolver
) : PlatformFileHandler {

    override fun read(file: PlatformFile): ByteReadChannel {
        return contentResolver.openInputStream(file.uri)!!.toByteReadChannel()
    }

    override suspend fun write(
        file: PlatformFile,
        channel: ByteReadChannel
    ) {
        contentResolver.openOutputStream(file.uri)!!.use {
            it.asByteWriteChannel().apply {
                channel.copyTo(this)
                flushAndClose()
            }
        }
    }

    override fun delete(file: PlatformFile) {
        contentResolver.delete(file.uri, null, null)
    }

}