package ua.syt0r.kanji.core.backup

import android.content.ContentResolver
import android.net.Uri
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class PlatformFileAndroid(
    val fileUri: Uri
) : PlatformFile

class AndroidPlatformFileHandler(
    private val contentResolver: ContentResolver
) : PlatformFileHandler {

    override fun getInputStream(file: PlatformFile): ByteReadChannel {
        file as PlatformFileAndroid
        return contentResolver.openInputStream(file.fileUri)!!.toByteReadChannel()
    }

    override fun getOutputStream(file: PlatformFile): ByteWriteChannel {
        file as PlatformFileAndroid

        val channel = ByteChannel(autoFlush = true)

        CoroutineScope(Dispatchers.IO).launch {
            contentResolver.openOutputStream(file.fileUri)!!.use { outputStream ->
                channel.copyTo(outputStream)
            }
        }

        return channel
    }

}
