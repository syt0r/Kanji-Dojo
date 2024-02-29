package ua.syt0r.kanji.core.japanese

import dev.esnault.wanakana.core.Wanakana
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.app_data.data.FuriganaStringCompound
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.buildFuriganaString

interface RomajiConverter {
    fun toRomaji(kanaText: String): String
}

fun RomajiConverter.getWordWithExtraRomajiReading(word: JapaneseWord): JapaneseWord {
    val kanaReading = word.readings
        .find { it.compounds.all { it.annotation == null } }
        ?: return word

    val romajiReadings = kanaReading.compounds
        .joinToString("") { it.text }
        .let { buildFuriganaString { append(toRomaji(it)) } }

    return word.copy(
        readings = listOf(romajiReadings) + word.readings
    )
}

fun RomajiConverter.getWordWithFuriganaForKana(word: JapaneseWord): JapaneseWord {
    val updatedReadings = word.readings.map {
        val isKanaReading = it.compounds.all { it.annotation == null }
        if (!isKanaReading) return@map it

        FuriganaString(
            compounds = it.compounds.map {
                FuriganaStringCompound(text = it.text, annotation = toRomaji(it.text))
            }
        )
    }

    return word.copy(readings = updatedReadings)
}

class WanakanaRomajiConverter : RomajiConverter {

    override fun toRomaji(kanaText: String): String {
        return Wanakana.toRomaji(kanaText)
    }

}