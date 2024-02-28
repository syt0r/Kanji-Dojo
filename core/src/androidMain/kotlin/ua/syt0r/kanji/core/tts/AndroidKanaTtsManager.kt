package ua.syt0r.kanji.core.tts

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.japanese.KanaReading
import kotlin.math.roundToLong


class AndroidKanaTtsManager(
    private val player: ExoPlayer,
    private val voiceData: KanaVoiceData
) : KanaTtsManager {

    private lateinit var romajiToMediaItem: Map<String, MediaItem>

    override suspend fun speak(reading: KanaReading): Unit = withContext(Dispatchers.Main) {
        if (!::romajiToMediaItem.isInitialized) {
            initializeVoice()
        }
        player.setMediaItem(romajiToMediaItem.getValue(reading.nihonShiki))
        player.prepare()
        player.play()
    }

    private fun initializeVoice() {
        val assetUri = Uri.parse("asset:///${voiceData.assetFileName}")
        val completeVoiceMediaItem = MediaItem.fromUri(assetUri)
        romajiToMediaItem = voiceData.clips.associate {
            val clippingConfigurationBuilder = MediaItem.ClippingConfiguration.Builder()

            clippingConfigurationBuilder.setStartPositionMs(it.clipStartSec.secondsToMillis())

            if (it.clipEndSec != null)
                clippingConfigurationBuilder.setEndPositionMs(it.clipEndSec.secondsToMillis())

            it.romaji to completeVoiceMediaItem.buildUpon()
                .setClippingConfiguration(clippingConfigurationBuilder.build())
                .build()
        }
    }

    private fun Double.secondsToMillis(): Long = (this * 1000).roundToLong()

}