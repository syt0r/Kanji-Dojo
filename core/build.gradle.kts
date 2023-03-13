import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight") version "2.0.0-alpha05"
}

@OptIn(ExperimentalComposeLibrary::class)
kotlin {
    jvm()
    android()
    sourceSets {
        val koinVersion = "3.2.0"
        val commonMain by getting {
            dependencies {
                api(project(":common"))
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.runtime)
                api("io.insert-koin:koin-core:$koinVersion")
                api("io.insert-koin:koin-androidx-compose:$koinVersion")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
                implementation("app.cash.sqldelight:android-driver:2.0.0-alpha05")

                val lifecycleVersion = "2.5.1"
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
                api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

                api("io.insert-koin:koin-android:$koinVersion")

                implementation("androidx.navigation:navigation-compose:2.5.3")
                api("androidx.datastore:datastore-preferences:1.0.0")
            }
        }
        val jvmMain by getting {
            resources.srcDir("$rootDir/app/src/main/assets")
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0-alpha05")
            }
        }
    }
}

sqldelight {
    databases {
        create("KanjiDatabase") {
            packageName.set("ua.syt0r.kanji.core.kanji_data.db")
            sourceFolders.set(listOf("kanji_data_sql"))
        }
        create("UserDataDatabase") {
            packageName.set("ua.syt0r.kanji.core.user_data.db")
            sourceFolders.set(listOf("user_data_sql"))
        }
    }
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 26
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "ua.syt0r.kanji.core"
}


compose.desktop {
    application {
        mainClass = "ua.syt0r.kanji.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "kanji"
            packageVersion = "1.0.0"
        }
    }
}