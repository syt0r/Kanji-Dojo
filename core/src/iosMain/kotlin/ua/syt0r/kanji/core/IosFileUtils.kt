package ua.syt0r.kanji.core

import io.ktor.http.decodeURLPart
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun getPrivateAppDataDirPath(): String {
    val bundleId = NSBundle.mainBundle.bundleIdentifier!!
    val url = NSFileManager.defaultManager
        .URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )!!
        .URLByAppendingPathComponent(
            pathComponent = bundleId,
            isDirectory = true
        )!!

    NSFileManager.defaultManager.createDirectoryAtURL(
        url = url,
        withIntermediateDirectories = true,
        attributes = null,
        error = null
    )

    return url.absoluteString()!!.formattedIosFilePath()
}

fun String.formattedIosFilePath(): String = removePrefix("file://").decodeURLPart()

fun FileSystem.deleteRecursively(path: Path, mustExist: Boolean = true) {
    val isDirectory = metadataOrNull(path)?.isDirectory == true
    if (isDirectory) {
        for (child in list(path)) {
            deleteRecursively(child, mustExist)
        }
    }
    delete(path, mustExist)
}