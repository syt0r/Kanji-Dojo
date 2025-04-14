import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CleanupLegacyAppAssetsTask : DefaultTask() {

    companion object {
        val name = "cleanupLegacyAppAssets"

        private val legacyPaths = listOf(
            "core/src/commonMain/resources",
            "core/src/androidMain/assets",
            "core/src/jvmMain/resources"
        )
    }

    @get:OutputDirectories
    val outputDirs = legacyPaths.map { File(it) }

    @TaskAction
    fun run() {
        outputDirs.forEach { it.deleteRecursively() }
    }

}