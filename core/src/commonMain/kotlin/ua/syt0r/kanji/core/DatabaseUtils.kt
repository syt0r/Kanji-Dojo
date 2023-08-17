package ua.syt0r.kanji.core

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer

@Throws(IOException::class)
fun readSqliteUserVersion(databaseFile: File): Int {
    FileInputStream(databaseFile).channel.use { input ->
        val buffer = ByteBuffer.allocate(4)
        input.tryLock(60, 4, true)
        input.position(60)
        val read = input.read(buffer)
        if (read != 4) {
            throw IOException("Bad database header, unable to read 4 bytes at offset 60")
        }
        buffer.rewind()
        return buffer.int // ByteBuffer is big-endian by default
    }
}