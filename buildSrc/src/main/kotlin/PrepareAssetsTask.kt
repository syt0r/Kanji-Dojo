import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized
import java.io.File


fun Project.registerPrepareAppAssetTasks() {
    PrepareAssetsTask.SourceSet.values().forEach { registerAppAssetTask(it) }
}

private fun Project.registerAppAssetTask(sourceSet: PrepareAssetsTask.SourceSet) {
    val cleanupTask = tasks.findByName(CleanupLegacyAppAssetsTask.name)
        ?: tasks.create(CleanupLegacyAppAssetsTask.name, CleanupLegacyAppAssetsTask::class.java)

    val composeResourcesDir = File(project.rootDir, "core/src/${sourceSet.title}/composeResources")
    val assetsDir = File(composeResourcesDir, "files")

    val prepareTask = tasks.create(
        "prepareKanjiDojoAssetsFor${sourceSet.title.capitalized()}",
        PrepareAssetsTask::class.java,
        {
            this.sourceSet = sourceSet
            this.assetsPath = assetsDir.path
        }
    )

    prepareTask.dependsOn(cleanupTask)

    val sourceSetTitle = "${sourceSet.title.capitalized()}"
    val dependentTasks = setOf(
        "copyNonXmlValueResourcesFor$sourceSetTitle",
        "prepareComposeResourcesTaskFor${sourceSetTitle}"
    )

    dependentTasks
        .map { tasks.findByName(it) ?: throw IllegalStateException("Task $it not found") }
        .forEach {
            println("Setting up dependency for ${it.name}")
            it.dependsOn(prepareTask)
            it.mustRunAfter(prepareTask)
        }

}

open class PrepareAssetsTask : DefaultTask() {

    enum class SourceSet(val assetLocation: AssetLocation) {

        Common(AppAssets.CommonAssetsLocation),
        Android(AppAssets.AndroidAssetsLocation),
        Jvm(AppAssets.DesktopAssetsLocation),
        iOS(AppAssets.IosAssetsLocation);

        val title: String = "${name.lowercase()}Main"

    }

    @Input
    lateinit var sourceSet: SourceSet

    @Input
    lateinit var assetsPath: String

    @get:OutputDirectory
    val output
        get() = assetsPath

    @TaskAction
    fun run() {
        println("Preparing Kanji Dojo Assets for $sourceSet at $assetsPath...")
        handleAssets(sourceSet.assetLocation)
    }

    private fun handleAssets(assetLocation: AssetLocation) {
        val assetsDir = File(assetsPath)
        if (!assetsDir.exists()) assetsDir.mkdirs()

        val expectedFileNames = assetLocation.expectedAssets.map { it.fileName }.toSet()
        val unexpectedFiles = assetsDir.listFiles()!!
            .filter { !expectedFileNames.contains(it.name) }

        if (unexpectedFiles.isNotEmpty()) {
            val unexpectedFileNames = unexpectedFiles.joinToString { it.name }
            println("Found ${unexpectedFiles.size} unknown assets [$unexpectedFileNames], removing...")

            unexpectedFiles.forEach { it.delete() }
        }

        assetLocation.expectedAssets.forEach { (fileName, url) ->
            url ?: return@forEach
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