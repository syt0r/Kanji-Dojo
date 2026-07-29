data class Asset(
    val fileName: String,
    val url: String?
)

data class AssetLocation(
    val expectedAssets: List<Asset>
)

object AppAssets {

    const val AppDataDatabaseVersion = 15
    const val AppDataAssetFileName = "kanji-dojo-data-base-v$AppDataDatabaseVersion.sql"

    val kanaVoiceOpus = Asset(
        fileName = "ja-JP-Neural2-B.opus",
        url = "https://github.com/syt0r/Kanji-Dojo-Data/releases/download/voice-v1/ja-JP-Neural2-B.opus"
    )

    val kanaVoiceWav = Asset(
        fileName = "ja-JP-Neural2-B.wav",
        url = "https://github.com/syt0r/Kanji-Dojo-Data/releases/download/voice-v1/ja-JP-Neural2-B.wav"
    )

    val CommonAssetsLocation = AssetLocation(
        expectedAssets = listOf(
            Asset(
                fileName = AppDataAssetFileName,
                url = "https://github.com/syt0r/Kanji-Dojo-Data/releases/download/v15.0/kanji-dojo-data-base-v15.sql"
            ),
            Asset(
                fileName = "text_analysis_preview.json",
                url = null
            ),
            // Bundled directory of pre-generated word pronunciation clips (see
            // core/src/commonMain/composeResources/files/word_voice/), committed directly to the
            // repo rather than downloaded -- null url means the prepare-assets task only needs to
            // leave it alone, not fetch it.
            Asset(
                fileName = "word_voice",
                url = null
            )
        )
    )

    val AndroidAssetsLocation = AssetLocation(
        expectedAssets = listOf(kanaVoiceOpus)
    )

    val DesktopAssetsLocation = AssetLocation(
        expectedAssets = listOf(kanaVoiceWav)
    )

    val IosAssetsLocation = AssetLocation(
        expectedAssets = listOf(kanaVoiceWav)
    )

}
