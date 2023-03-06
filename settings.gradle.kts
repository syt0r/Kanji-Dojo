pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    val kotlinVersion = "1.7.20"
    val agpVersion = "7.3.1"
    val composeVersion = "1.3.0"

    plugins {
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)
        kotlin("plugin.serialization") version kotlinVersion
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)
        id("org.jetbrains.compose").version(composeVersion)
        id("com.google.dagger.hilt.android").version("2.45")
        id("com.google.gms.google-services").version("4.3.14")
        id("com.google.firebase.crashlytics").version("2.8.1")
        kotlin("plugin.serialization") version "1.7.20"
        kotlin("plugin.parcelize") version "1.7.20"
    }
}

rootProject.name = "kanji-dojo"
include(":app", ":common", ":parser", ":core")