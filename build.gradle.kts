// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.dedekt) apply false
    alias(libs.plugins.dokka.documentation) apply false
    alias(libs.plugins.sonarqube) apply false
    alias(libs.plugins.dependency.check.gradle) apply false
}
