package ua.syt0r.kanji.core.tts

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.japanese.KanaReading
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class JavaKanaTtsManager(
    private val voiceData: KanaVoiceData,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : KanaTtsManager {

    @OptIn(ExperimentalResourceApi::class)
    private val asyncAudioBytes = CoroutineScope(dispatcher).async(
        start = CoroutineStart.LAZY
    ) {
        Res.readBytes(voiceData.assetPath)
    }

    override suspend fun speak(reading: KanaReading): Unit = with(dispatcher) {
        val inputStream = ByteArrayInputStream(asyncAudioBytes.await())
        val audioStream = AudioSystem.getAudioInputStream(inputStream)

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