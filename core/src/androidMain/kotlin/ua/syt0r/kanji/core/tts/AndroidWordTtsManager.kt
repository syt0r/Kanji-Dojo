package ua.syt0r.kanji.core.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

class AndroidWordTtsManager(context: Context) : WordTtsManager {

    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.JAPAN
            }
        }
    }

    override suspend fun speak(word: String) {
        val tts = textToSpeech ?: return
        val utteranceId = UUID.randomUUID().toString()

        suspendCancellableCoroutine { continuation ->
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit

                override fun onDone(utteranceId: String?) {
                    if (continuation.isActive) continuation.resume(Unit)
                }

                @Deprecated("Deprecated in TextToSpeech")
                override fun onError(utteranceId: String?) {
                    if (continuation.isActive) continuation.resume(Unit)
                }
            })
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    override suspend fun isAvailable(): Boolean {
        val tts = textToSpeech ?: return false
        val result = tts.isLanguageAvailable(Locale.JAPAN)
        return result == TextToSpeech.LANG_AVAILABLE ||
            result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
            result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
    }

    override val unavailableMessage: String
        get() = "No Japanese voice found on this device. You can install one from " +
            "Settings > Accessibility > Text-to-speech output > Preferred engine > Install voice data."

}
