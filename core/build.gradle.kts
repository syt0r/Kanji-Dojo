import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.LONG
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.codingfeline.buildkonfig")
    id("app.cash.sqldelight")
}


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
                api(compose.materialIconsExtended)
                api("io.insert-koin:koin-core:$koinVersion")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:android-driver:2.0.0-alpha05")

                val lifecycleVersion = "2.6.1"
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
                api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

                api("io.insert-koin:koin-android:$koinVersion")
                api("io.insert-koin:koin-androidx-compose:$koinVersion")

                implementation("androidx.navigation:navigation-compose:2.7.1")
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.datastore:datastore-preferences:1.0.0")
                api(compose.uiTooling)

                api("androidx.core:core-ktx:1.9.0")
                api("androidx.appcompat:appcompat:1.6.1")
            }
        }
        val jvmMain by getting {
            resources.srcDir("$rootDir/app/src/main/assets")
            dependencies {
                implementation(compose.desktop.currentOs)
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
    namespace = "ua.syt0r.kanji.core"

    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.5.2")
}

compose.desktop {
    application {
        mainClass = "ua.syt0r.kanji.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "kanji"
            packageVersion = AppVerion.desktopAppVersion
        }
    }
}

buildkonfig {
    packageName = "ua.syt0r.kanji"
    defaultConfigs {
        buildConfigField(LONG, "versionCode", AppVerion.versionCode.toString())
        buildConfigField(STRING, "versionName", AppVerion.versionName)
    }
}