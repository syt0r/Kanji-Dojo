package ua.syt0r.kanji.core.tts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import ua.syt0r.kanji.core.japanese.KanaReading
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class JavaKanaTtsManager(
    private val voiceData: KanaVoiceData
) : KanaTtsManager {

    override suspend fun speak(reading: KanaReading): Unit = with(Dispatchers.IO) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(voiceData.assetFileName)!!
        val audioStream = AudioSystem.getAudioInputStream(BufferedInputStream(inputStream))

        val clip = AudioSystem.getClip()
        clip.open(audioStream)

        val frameRate = audioStream.format.frameRate
        val clipData = voiceData.clips.find { it.romaji == reading.nihonShiki }!!

        val startFrame = (clipData.clipStartSec * frameRate).roundToInt()
        clip.framePosition = startFrame
        clip.start()

        clipData.clipEndSec?.let {
            val lengthSec = it - clipData.clipStartSec
            delay((lengthSec * 1000).roundToLong())
            clip.stop()
        }
    }

}