package ua.syt0r.kanji.core

import app.cash.sqldelight.db.SqlDriver
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

suspend fun SqlDriver.readUserVersion(): Int {
    val queryResult = executeQuery(
        identifier = null,
        sql = "PRAGMA user_version;",
        mapper = { sqlCursor -> sqlCursor.getLong(0)!!.toInt() },
        parameters = 0
    )
    return queryResult.await()
}

suspend fun SqlDriver.setUserVersion(version: Int) {
    execute(null, "PRAGMA user_version = $version;", 0).await()
}
