// ── Gradle Settings ─────────────────────────────────────────────────────
//
// Plugin and dependency resolution repositories. Google and Maven Central
// cover everything in the version catalog — no custom or snapshot repos.
// The Gradle Plugin Portal is needed for the Kotlin Multiplatform plugin
// (even though this project is Android-only, KSP's plugin resolver requires it).

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "inkwell-android"
include(":app")
