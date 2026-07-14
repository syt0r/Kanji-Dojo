package ua.syt0r.kanji.core.tts

/**
 * Speaks a whole Japanese word/reading in one go (e.g. a kanji's on'yomi/kun'yomi), using a real
 * speech synthesis engine on platforms that have one. This is intentionally separate from
 * [KanaTtsManager], which plays back pre-recorded single-mora clips for the kana practice quiz
 * and isn't a real TTS engine, so it can't pronounce a full word naturally.
 */
interface WordTtsManager {

    suspend fun speak(word: String)

    /**
     * Whether a Japanese voice is currently available to speak with. On Android/iOS this is
     * effectively always true (bundled/downloadable system voices). On Desktop/JVM this checks
     * whether the OS itself has a Japanese voice installed, since the JVM has no speech engine
     * of its own - this can be false, most commonly on Windows if the user never installed the
     * optional Japanese language/speech pack.
     */
    suspend fun isAvailable(): Boolean

    /**
     * User-facing explanation to show when [isAvailable] is false, including instructions to fix
     * it where applicable (e.g. how to install the missing OS voice).
     */
    val unavailableMessage: String

}
