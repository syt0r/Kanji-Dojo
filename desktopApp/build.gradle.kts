import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("org.jetbrains.compose.hot-reload")
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {

    jvm()

    jvmToolchain(17)
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }

    sourceSets {

        jvmMain {
            dependencies {
                implementation(compose.components.resources)
                implementation(project(":core"))
            }
        }

    }

}

val mainClassKt = "ua.syt0r.kanji.desktopApp.MainKt"

compose.desktop {
    application {
        mainClass = mainClassKt
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "Kanji Dojo"
            packageVersion = AppVersion.desktopAppVersion
            vendor = "syt0r"

            modules("jdk.unsupported", "java.sql")

            windows {
                upgradeUuid = "12c852a8-6e21-41a7-bd47-3bec9ff5c5df"
                iconFile.set(File("windows_icon.ico"))
                menu = true
                shortcut = true
            }

            macOS {
                bundleID = "ua.syt0r.kanji-dojo"
                iconFile.set(File("mac_icon.icns"))
            }

            linux {
                val linuxIcon = File("src/jvmMain/composeResources/drawable/windowIcon.png")
                iconFile.set(linuxIcon)
            }

        }
    }
}

tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set(mainClassKt)
    javaLauncher
}

compose.resources {
    generateResClass = always
    packageOfResClass = "ua.syt0r.kanji.desktopApp"
}

aboutLibraries {
    configPath = "core/credits"
    excludeFields = arrayOf("generated")
}
