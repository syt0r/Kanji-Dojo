package ua.syt0r.kanji.core.tts

import ua.syt0r.kanji.core.japanese.KanaReading

interface KanaVoiceData {
    val assetFileName: String
    val clips: List<KanaCharacterVoiceClipData>
}

data class KanaCharacterVoiceClipData(
    val romaji: String,
    val clipStartSec: Double,
    val clipEndSec: Double?
)

interface KanaTtsManager {
    suspend fun speak(reading: KanaReading)
}