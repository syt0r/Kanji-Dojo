package ua.syt0r.kanji.core

import java.io.File

fun getUserDataDirectory(): File {
    val osName = System.getProperty("os.name")
    val userDir = System.getProperty("user.home")
    return when {
        osName.contains("linux", true) -> {
            File("$userDir/.local/share/kanji-dojo/")
        }
        else -> File("$userDir/KanjiDojo")
    }
}