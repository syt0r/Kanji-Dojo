import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

data class KanjiDojoAssetLocation(
    val directory: String,
    val expectedFiles: Map<String, String>
)

open class PrepareKanjiDojoAssetsTask : DefaultTask() {

    companion object {
        const val AppDataAssetFileName = "kanji-dojo-data-base-v7.sql"
        const val KanaVoice1AndroidFileName = "ja-JP-Neural2-B.opus"
        const val KanaVoice1JvmFileName = "ja-JP-Neural2-B.wav"

        private val commonAssetLocation = KanjiDojoAssetLocation(
            directory = "core/src/commonMain/resources",
            expectedFiles = mapOf(
                AppDataAssetFileName to "https://github.com/syt0r/Kanji-Dojo-Data/releases/download/v7.0/kanji-dojo-data-base-v7.sql"
            )
        )

        private val androidAssetLocation = KanjiDojoAssetLocation(
            directory = "core/src/androidMain/assets",
            expectedFiles = mapOf(
                KanaVoice1AndroidFileName to "https://github.com/syt0r/Kanji-Dojo-Data/releases/download/voice-v1/ja-JP-Neural2-B.opus"
            )
        )

        private val desktopAssetLocation = KanjiDojoAssetLocation(
            directory = "core/src/jvmMain/resources",
            expectedFiles = mapOf(
                KanaVoice1JvmFileName to "https://github.com/syt0r/Kanji-Dojo-Data/releases/download/voice-v1/ja-JP-Neural2-B.wav"
            )
        )
    }

    enum class Platform(val assetLocation: KanjiDojoAssetLocation) {
        Android(androidAssetLocation),
        Desktop(desktopAssetLocation)
    }

    @Input
    lateinit var platform: Platform

    @TaskAction
    fun download() {
        println("Preparing Kanji Dojo Assets for $platform...")
        handleAssets(commonAssetLocation)
        handleAssets(platform.assetLocation)
    }

    private fun handleAssets(assetLocation: KanjiDojoAssetLocation) {
        val assetsDir = File(project.rootDir, assetLocation.directory)
        if (!assetsDir.exists()) assetsDir.mkdirs()

        assetLocation.expectedFiles.forEach { (fileName, url) ->
            val assetFile = File(assetsDir, fileName)
            if (!assetFile.exists()) {
                println("Asset $fileName not found, downloading")
                downloadFile(assetFile, url)
            } else {
                println("Skipping $fileName downloading, already exist")
            }
        }
    }

    private fun downloadFile(file: File, url: String) {
        ant.invokeMethod("get", mapOf("src" to url, "dest" to file))
    }

}