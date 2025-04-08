plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvmToolchain(17)
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }

}

dependencies {
    commonMainImplementation(compose.components.resources)
    commonMainImplementation(project(":core"))
}

compose.resources {
    generateResClass = always
    packageOfResClass = "ua.syt0r.kanji.ios"
}

aboutLibraries {
    configPath = "core/credits"
    excludeFields = arrayOf("generated")
}
