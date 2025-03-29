package ua.syt0r.kanji.core.backup

import android.content.ContentResolver
import androidx.core.net.toFile
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.streams.asByteWriteChannel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import ua.syt0r.kanji.core.file.PlatformFile
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.inputStream
import kotlin.use

class AndroidBackupArchiveHandler(
    private val contentResolver: ContentResolver
) : BackupArchiveHandler {

    private val json = Json { prettyPrint = true }

    override suspend fun writeBackupZip(
        backupInfo: BackupInfo,
        preferences: JsonObject,
        database: PlatformFile,
        output: PlatformFile
    ) {
        contentResolver.openOutputStream(output.uri).use {
            ZipOutputStream(it).use {
                it.writeJsonFile(BackupArchiveSchema.BACKUP_INFO_FILENAME, backupInfo)
                it.writeJsonFile(BackupArchiveSchema.PREFERENCES_FILENAME, preferences)
                it.writeFile(backupInfo.userDatabaseFileName, database.uri.toFile())
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readZipBackupInfo(file: PlatformFile): BackupInfo {
        return file.withZipEntry(BackupArchiveSchema.BACKUP_INFO_FILENAME) {
            json.decodeFromStream<BackupInfo>(this)
        }
    }

    override suspend fun readBackupZip(
        file: PlatformFile,
        action: suspend (BackupArchiveData) -> Unit
    ) {
        val backupInfo = readZipBackupInfo(file)

        val preferences: JsonObject = file.withZipEntry(BackupArchiveSchema.PREFERENCES_FILENAME) {
            json.decodeFromStream(this)
        }

        file.withZipEntry(backupInfo.userDatabaseFileName) {
            val backupArchiveData = BackupArchiveData(
                backupInfo,
                preferences,
                toByteReadChannel()
            )
            action(backupArchiveData)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> ZipOutputStream.writeJsonFile(fileName: String, data: T) {
        putNextEntry(ZipEntry(fileName))
        Json.encodeToStream(data, this)
        flush()
        closeEntry()
    }

    private suspend fun ZipOutputStream.writeFile(name: String, file: File) {
        putNextEntry(ZipEntry(name))
        val readChannel = file.inputStream().toByteReadChannel()
        val writeChannel = asByteWriteChannel()
        readChannel.copyTo(writeChannel)
        writeChannel.flush()
        closeEntry()
    }

    private inline fun <T> PlatformFile.withZipEntry(
        name: String,
        action: InputStream.(ZipEntry) -> T
    ): T {
        return contentResolver.openInputStream(uri).use {
            ZipInputStream(it).use {
                var currentEntry = it.nextEntry

                while (currentEntry != null && currentEntry.name != name) {
                    currentEntry = it.nextEntry
                }

                currentEntry ?: error("No zip entry with name[$name] found")
                action(it, currentEntry)
            }
        }
    }

}