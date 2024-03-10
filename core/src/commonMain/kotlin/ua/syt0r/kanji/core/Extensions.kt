package ua.syt0r.kanji.core

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private const val DEFAULT_BUFFER_SIZE = 8192

@Throws(IOException::class)
fun InputStream.transferToCompat(out: OutputStream): Long {
    var transferred: Long = 0
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var read: Int
    while (read(buffer, 0, DEFAULT_BUFFER_SIZE).also { read = it } >= 0) {
        out.write(buffer, 0, read)
        transferred += read.toLong()
    }
    return transferred
}