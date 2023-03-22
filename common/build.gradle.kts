plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    kotlin("plugin.parcelize")
}

kotlin {
    jvm()
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
            }
        }
        val androidMain by getting
        val jvmMain by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "ua.syt0r.kanji.common"

    defaultConfig {
        consumerProguardFile("proguard-rules.pro")
    }
}