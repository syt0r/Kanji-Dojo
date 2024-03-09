package ua.syt0r.kanji.core.backup

import android.content.ContentResolver
import android.net.Uri
import java.io.InputStream
import java.io.OutputStream

data class PlatformFileAndroid(
    val fileUri: Uri
) : PlatformFile

class AndroidPlatformFileHandler(
    private val contentResolver: ContentResolver
) : PlatformFileHandler {

    override fun getInputStream(file: PlatformFile): InputStream {
        file as PlatformFileAndroid
        return contentResolver.openInputStream(file.fileUri)!!
    }

    override fun getOutputStream(file: PlatformFile): OutputStream {
        file as PlatformFileAndroid
        return contentResolver.openOutputStream(file.fileUri)!!
    }

}
