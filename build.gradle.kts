// build.gradle.kts (Project-level)

plugins {

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
