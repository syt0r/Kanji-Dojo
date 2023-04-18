pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    val kotlinVersion = "1.7.20"
    val agpVersion = "7.3.1"
    val composeVersion = "1.4.0"

    plugins {
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)
        kotlin("plugin.serialization") version kotlinVersion
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)
        id("org.jetbrains.compose").version(composeVersion)
        id("com.google.gms.google-services").version("4.3.14")
        id("com.google.firebase.crashlytics").version("2.8.1")
        kotlin("plugin.serialization") version "1.7.20"
        id("com.codingfeline.buildkonfig") version "0.13.3"
        id("app.cash.sqldelight") version "2.0.0-alpha05"
    }
}

rootProject.name = "kanji-dojo"
include(":app", ":common", ":parser", ":core")