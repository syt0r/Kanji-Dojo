package ua.syt0r.kanji.core.tts

import ua.syt0r.kanji.BuildKonfig

object Neural2BKanaVoiceData : KanaVoiceData {

    override val assetFileName: String = BuildKonfig.kanaVoiceAssetName

    override val clips: List<KanaCharacterVoiceClipData>
        get() = timestamps.mapIndexed { index, (romaji, startSec) ->
            KanaCharacterVoiceClipData(
                romaji = romaji,
                clipStartSec = startSec,
                clipEndSec = timestamps.getOrNull(index + 1)?.second
            )
        }

}

private val timestamps = listOf(
    "a" to 0.240873,
    "i" to 0.924801,
    "u" to 1.674406,
    "e" to 2.454126,
    "o" to 3.188186,
    "ka" to 3.887367,
    "ki" to 4.613996,
    "ku" to 5.374655,
    "ke" to 6.103286,
    "ko" to 6.851934,
    "sa" to 7.592576,
    "shi" to 8.409283,
    "su" to 9.221987,
    "se" to 10.114760,
    "so" to 10.889431,
    "ta" to 11.732161,
    "chi" to 12.494768,
    "tsu" to 13.302766,
    "te" to 14.115632,
    "to" to 14.808826,
    "na" to 15.536930,
    "ni" to 16.374133,
    "nu" to 17.177514,
    "ne" to 18.033937,
    "no" to 18.881935,
    "ha" to 19.742415,
    "hi" to 20.457931,
    "fu" to 21.217443,
    "he" to 22.042293,
    "ho" to 22.718091,
    "ma" to 23.470585,
    "mi" to 24.282410,
    "mu" to 25.073976,
    "me" to 25.875672,
    "mo" to 26.641190,
    "ra" to 27.418285,
    "ri" to 28.192486,
    "ru" to 28.982605,
    "re" to 29.795878,
    "ro" to 30.571526,
    "ya" to 31.383351,
    "yu" to 32.232801,
    "yo" to 33.046074,
    "wa" to 33.873818,
    "wo" to 34.682749,
    "n" to 35.407748,
    "ga" to 36.164584,
    "gi" to 36.950361,
    "gu" to 37.742279,
    "ge" to 38.569748,
    "go" to 39.372878,
    "za" to 40.185744,
    "zi" to 41.003477,
    "zu" to 41.821210,
    "ze" to 42.702220,
    "zo" to 43.554026,
    "da" to 44.400964,
    "di" to 45.228432,
    "du" to 46.055900,
    "de" to 46.893103,
    "do" to 47.710836,
    "ba" to 48.518834,
    "bi" to 49.331700,
    "bu" to 50.076421,
    "be" to 50.860082,
    "bo" to 51.643743,
    "pa" to 52.427404,
    "pi" to 53.157523,
    "pu" to 53.863305,
    "pe" to 54.603158,
    "po" to 55.357615
)
