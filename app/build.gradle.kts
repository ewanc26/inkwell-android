// ── App Module ──────────────────────────────────────────────────────────
//
// Build configuration for the Inkwell Android app. Targets API 26 (Android 8.0)
// as the minimum, which covers the kotlinx.serialization and Compose runtime
// requirements while still allowing access to ~95% of active devices.

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "uk.ewancroft.inkwell"
    compileSdk = 35

    defaultConfig {
        applicationId = "uk.ewancroft.inkwell"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Used by AppAuth for the OAuth redirect URI scheme
        manifestPlaceholders["appAuthRedirectScheme"] = "uk.ewancroft.inkwell"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
        debug {
            isDebuggable = true
            // Separate install from release builds for side-by-side testing
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

// ── Dependencies ────────────────────────────────────────────────────────

dependencies {
    // -- Compose BOM --
    // Single BOM import controls all Compose library versions
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.foundation)
    debugImplementation(libs.compose.ui.tooling)

    // -- Activity & Navigation --
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)

    // -- Lifecycle --
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // -- Network --
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // -- Serialization --
    implementation(libs.kotlinx.serialization.json)

    // -- Coroutines --
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // -- Data --
    implementation(libs.datastore.preferences)

    // -- Background --
    implementation(libs.workmanager)

    // -- Image Loading --
    implementation(libs.coil.compose)

    // -- DI --
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // -- Security (Credential Manager) --
    implementation(libs.credentials)
}
