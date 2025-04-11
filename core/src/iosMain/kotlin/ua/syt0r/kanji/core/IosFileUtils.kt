package ua.syt0r.kanji.core

import io.ktor.http.decodeURLPart
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun getPrivateAppDataDirPath(): String {
    val bundleId = NSBundle.mainBundle.bundleIdentifier
    val path = NSFileManager.defaultManager
        .URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask)
        .first()
        .toString()
        .plus(bundleId)
        .formattedIosFilePath()

    NSFileManager.defaultManager.createDirectoryAtURL(
        url = NSURL.URLWithString(path)!!,
        withIntermediateDirectories = true,
        attributes = null,
        error = null
    )

    return path
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