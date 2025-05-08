pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    val kotlinVersion = "2.1.20"
    val agpVersion = "8.5.2"

    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("android") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.compose") version kotlinVersion
        id("com.android.application") version agpVersion
        id("com.android.library") version agpVersion
        id("org.jetbrains.compose") version "1.8.0"
        id("org.jetbrains.compose.hot-reload") version "1.0.0-alpha09"
        id("com.google.gms.google-services") version "4.4.2"
        id("com.google.firebase.crashlytics") version "3.0.2"
        id("com.codingfeline.buildkonfig") version "0.13.3"
        id("app.cash.sqldelight") version "2.0.2"
        id("com.mikepenz.aboutlibraries.plugin") version "12.0.0"
    }
}

rootProject.name = "kanji-dojo"
include(":app", ":iosApp", ":desktopApp", ":core", ":mediaGenerator")
