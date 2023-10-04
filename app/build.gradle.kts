import com.android.build.api.dsl.ApplicationBuildType

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

    val keystoreFile = rootProject.file("keystore.jks")

    signingConfigs {
        create("signedBuild") {
            storeFile = keystoreFile
            System.getenv("KEYSTORE_PASS")?.let { storePassword = it }
            System.getenv("SIGN_KEY")?.let { keyAlias = it }
            System.getenv("SIGN_PASS")?.let { keyPassword = it }
        }
    }

    compileSdk = 34
    defaultConfig {
        applicationId = "ua.syt0r.kanji"
        minSdk = 26
        targetSdk = 34
        versionCode = AppVersion.versionCode
        versionName = AppVersion.versionName
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions.jvmTarget = "17"

    buildTypes {
        val debug = getByName("debug") {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".dev"
        }

        val release = getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    fun ApplicationBuildType.applySigningConfig() {
        if (keystoreFile.exists()) {
            signingConfig = signingConfigs.getByName("signedBuild")
        }
    }

    buildTypes.forEach { it.applySigningConfig() }

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
        kotlinCompilerExtensionVersion = "1.5.2"
    }

}

dependencies {
    implementation(project(":core"))

    // Google Play
    "googlePlayImplementation"(platform("com.google.firebase:firebase-bom:28.0.1"))
    "googlePlayImplementation"("com.google.firebase:firebase-analytics-ktx")
    "googlePlayImplementation"("com.google.firebase:firebase-crashlytics-ktx")
    "googlePlayImplementation"("com.google.android.play:review-ktx:2.0.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")

}
