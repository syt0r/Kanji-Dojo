package ua.syt0r.kanji.core.file

import kotlinx.io.files.Path

actual class PlatformFile(
    val url: String
) {

    val path = Path(url)

}