@file:OptIn(ExperimentalBuildToolsApi::class)

import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    alias(libs.plugins.build.config)
    id("app.cash.sqldelight")
}

kotlin {

    jvm()
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvmToolchain(17)
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.runtime)
                api(compose.materialIconsExtended)
                api(compose.components.resources)

                api(libs.koin.core)
                api(libs.koin.compose)
                api(libs.koin.compose.viewmodel)

                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.reflect)

                implementation(libs.datastore.preferences.core)
                implementation(libs.wanakana.core)

                api(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.auth)

                api(libs.aboutlibraries.core)
                implementation(libs.aboutlibraries.compose.m3)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.android.driver)

                api(libs.lifecycle.viewmodel.ktx)
                api(libs.lifecycle.livedata.ktx)

                implementation(libs.work.runtime.ktx)

                api(libs.koin.android)
                api(libs.koin.androidx.compose)

                implementation(libs.navigation.compose)
                api(libs.activity.compose)
                api(libs.datastore.preferences)
                api(compose.uiTooling)

                api(libs.core.ktx)
                api(libs.appcompat)
                implementation(libs.media3.exoplayer)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.sqldelight.jvm.sqlite.driver)
                implementation(libs.ktor.server.netty)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.sqldelight.native.sqlite.driver)
            }
        }
    }
}

compose.resources {
    generateResClass = always
    packageOfResClass = "ua.syt0r.kanji"
    publicResClass = true
}

sqldelight {
    linkSqlite = true
    databases {
        create("AppDataDatabase") {
            packageName.set("ua.syt0r.kanji.core.app_data.db")
            srcDirs("src/commonMain/sqldelight_app_data")
        }
        create("UserDataDatabase") {
            packageName.set("ua.syt0r.kanji.core.user_data.db")
            srcDirs("src/commonMain/sqldelight_user_data")
        }
    }
}

android {
    namespace = "ua.syt0r.kanji.core"

    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        assets.srcDir("src/commonMain/resources")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            consumerProguardFile("consumer-rules.pro")
        }
    }

}

buildConfig {

    packageName = "ua.syt0r.kanji"

    buildConfigField("versionCode", AppVersion.versionCode.toLong())
    buildConfigField("versionName", AppVersion.versionName)
    buildConfigField("appDataAssetName", AppAssets.AppDataAssetFileName)
    buildConfigField("appDataDatabaseVersion", AppAssets.AppDataDatabaseVersion)

    val kanaVoiceFieldName = "kanaVoiceAssetName"
    sourceSets.getByName("androidMain") {
        buildConfigField(
            name = kanaVoiceFieldName,
            value = AppAssets.KanaVoice1AndroidFileName
        )
    }
    sourceSets.getByName("jvmMain") {
        buildConfigField(
            name = kanaVoiceFieldName,
            value = AppAssets.KanaVoice1JvmFileName
        )
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Desktop
val prepareAssetsTaskDesktop = task<PrepareAssetsTask>("prepareKanjiDojoAssetsDesktop") {
    platform = PrepareAssetsTask.Platform.Desktop
}
project.tasks.findByName("jvmProcessResources")!!.dependsOn(prepareAssetsTaskDesktop)

// Android
val prepareAssetsTaskAndroid = task<PrepareAssetsTask>("prepareKanjiDojoAssetsAndroid") {
    platform = PrepareAssetsTask.Platform.Android
}
project.tasks.findByName("preBuild")!!.dependsOn(prepareAssetsTaskAndroid)
