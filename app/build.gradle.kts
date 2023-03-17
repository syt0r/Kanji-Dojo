plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

project.gradle.taskGraph.whenReady {
    allTasks.forEach { task ->

        val isFdroid = task.name.contains("fdroid", ignoreCase = true)

        val isGoogleTask = task.name.contains("GoogleServices", ignoreCase = true) ||
                task.name.contains("Crashlytics", ignoreCase = true)

        val isArtProfileTask = task.name.contains("ArtProfile", ignoreCase = true)

        if (isFdroid && (isGoogleTask || isArtProfileTask)) {
            println("Disabling f-droid task: ${task.name}")
            task.enabled = false
        }

    }
}

android {

    namespace = "ua.syt0r.kanji"

    compileSdk = 33
    defaultConfig {
        applicationId = "ua.syt0r.kanji"
        minSdk = 26
        targetSdk = 33
        versionCode = AppVerion.versionCode
        versionName = AppVerion.versionName
    }

    buildTypes {
        val debug = getByName("debug") {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".dev"
        }

        val release = getByName("release") {
            signingConfig = debug.signingConfig
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("googlePlay") {
            dimension = "version"
        }

        create("fdroid") {
            dimension = "version"
            applicationIdSuffix = ".fdroid"
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

}

dependencies {

    implementation(project(":core"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("androidx.compose.ui:ui-tooling:1.3.3")

    // Google Play
    "googlePlayImplementation"(platform("com.google.firebase:firebase-bom:28.0.1"))
    "googlePlayImplementation"("com.google.firebase:firebase-analytics-ktx")
    "googlePlayImplementation"("com.google.firebase:firebase-crashlytics-ktx")
    "googlePlayImplementation"("com.google.android.play:review-ktx:2.0.0")

    // Fdroid
    "fdroidImplementation"("com.github.matomo-org:matomo-sdk-android:4.1.4")

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")

}
