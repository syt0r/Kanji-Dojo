package ua.syt0r.kanji.core.tts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.japanese.getKanaReading
import ua.syt0r.kanji.core.japanese.isKana
import java.util.concurrent.TimeUnit

private enum class DesktopOs { WINDOWS, MACOS, OTHER }

private fun detectOs(): DesktopOs {
    val name = System.getProperty("os.name")?.lowercase().orEmpty()
    return when {
        name.contains("win") -> DesktopOs.WINDOWS
        name.contains("mac") -> DesktopOs.MACOS
        else -> DesktopOs.OTHER
    }
}

/**
 * Desktop/JVM has no bundled Japanese speech engine, but the OS itself often does: Windows ships
 * SAPI voices (if the optional Japanese language pack is installed) and macOS ships `say` voices.
 * This shells out to those instead of bundling a synthesis engine. On Linux, or if no Japanese
 * voice is installed, [isAvailable] reports false and [speak] falls back to reading the word one
 * kana at a time with the existing kana practice clip system, rather than staying silent.
 */
class JavaWordTtsManager(
    private val kanaFallback: KanaTtsManager
) : WordTtsManager {

    private val os = detectOs()

    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        when (os) {
            DesktopOs.WINDOWS -> hasWindowsJapaneseVoice()
            DesktopOs.MACOS -> hasMacJapaneseVoice()
            DesktopOs.OTHER -> false
        }
    }

    override suspend fun speak(word: String): Unit = withContext(Dispatchers.IO) {
        val spoken = when (os) {
            DesktopOs.WINDOWS -> speakWindows(word)
            DesktopOs.MACOS -> speakMac(word)
            DesktopOs.OTHER -> false
        }
        if (!spoken) {
            word.forEach { char ->
                if (char.isKana()) kanaFallback.speak(getKanaReading(char))
            }
        }
    }

    override val unavailableMessage: String
        get() = when (os) {
            DesktopOs.WINDOWS -> "No Japanese voice found on this PC. Go to " +
                "Settings > Time & Language > Language & region > Add a language, install " +
                "\"Japanese\" and make sure to include its speech/text-to-speech feature, " +
                "then restart Kanji Dojo."

            DesktopOs.MACOS -> "No Japanese voice found on this Mac. Go to " +
                "System Settings > Accessibility > Spoken Content > System Voice > " +
                "Manage Voices, then download a Japanese voice."

            DesktopOs.OTHER -> "No system Japanese voice could be found on this platform."
        }

    // --- Windows: PowerShell + System.Speech (bundled with .NET Framework since Windows 7) ---

    private fun hasWindowsJapaneseVoice(): Boolean {
        val output = runPowerShell(checkWindowsVoiceScript) ?: return false
        return output.trim() == "AVAILABLE"
    }

    private fun speakWindows(word: String): Boolean {
        if (!hasWindowsJapaneseVoice()) return false
        return runPowerShell(speakWindowsScript(word)) != null
    }

    private val checkWindowsVoiceScript =
        "Add-Type -AssemblyName System.Speech; " +
            "\$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
            "\$voice = \$synth.GetInstalledVoices() | Where-Object { \$_.VoiceInfo.Culture.Name -eq 'ja-JP' } | Select-Object -First 1; " +
            "if (\$voice) { Write-Output 'AVAILABLE' } else { Write-Output 'UNAVAILABLE' }"

    private fun speakWindowsScript(word: String): String {
        // Escape single quotes for PowerShell's single-quoted string literal.
        val escapedWord = word.replace("'", "''")
        return "Add-Type -AssemblyName System.Speech; " +
            "\$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
            "\$voice = \$synth.GetInstalledVoices() | Where-Object { \$_.VoiceInfo.Culture.Name -eq 'ja-JP' } | Select-Object -First 1; " +
            "if (\$voice) { \$synth.SelectVoice(\$voice.VoiceInfo.Name); \$synth.Speak('$escapedWord') }"
    }

    private fun runPowerShell(script: String): String? =
        runCommand(listOf("powershell", "-NoProfile", "-NonInteractive", "-Command", script))

    // --- macOS: the `say` command, using whichever installed voice is set to Japanese ---

    private fun hasMacJapaneseVoice(): Boolean = findMacJapaneseVoiceName() != null

    private fun speakMac(word: String): Boolean {
        val voiceName = findMacJapaneseVoiceName() ?: return false
        return runCommand(listOf("say", "-v", voiceName, word)) != null
    }

    private fun findMacJapaneseVoiceName(): String? {
        val output = runCommand(listOf("say", "-v", "?")) ?: return null
        // Each line looks like: "Kyoko              ja_JP    # こんにちは..."
        return output.lineSequence()
            .firstOrNull { it.contains("ja_JP") }
            ?.trim()
            ?.substringBefore(' ')
            ?.takeIf { it.isNotBlank() }
    }

    // --- Shared process execution helper ---

    private fun runCommand(command: List<String>): String? {
        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            val exited = process.waitFor(10, TimeUnit.SECONDS)
            if (!exited) {
                process.destroyForcibly()
                null
            } else if (process.exitValue() != 0) {
                null
            } else {
                output
            }
        } catch (e: Exception) {
            null
        }
    }

}
