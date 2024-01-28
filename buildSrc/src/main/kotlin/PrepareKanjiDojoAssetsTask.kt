import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

private val kanjiDojoAssetsPath = "core/src/commonMain/resources"

open class PrepareKanjiDojoAssetsTask : DefaultTask() {

    private val assetFileNameToUrl = mapOf(
        "kanji-dojo-data-base-v6.sql" to "https://github.com/syt0r/Kanji-Dojo-Data/releases/download/v6.2/kanji-dojo-data-base-v6.sql"
    )

    @TaskAction
    fun download() {
        val assetsDir = File(project.rootDir, kanjiDojoAssetsPath)
        println("Preparing Kanji Dojo Assets...")
        if (!assetsDir.exists()) assetsDir.mkdirs()
        assetFileNameToUrl.forEach { fileName, url ->
            val assetFile = File(assetsDir, fileName)
            if (!assetFile.exists()) {
                println("Asset $fileName not found, downloading")
                download(assetFile, url)
            } else {
                println("Skipping $fileName downloading, already exist")
            }
        }
    }

    private fun download(file: File, url: String) {
        ant.invokeMethod("get", mapOf("src" to url, "dest" to file))
    }

}