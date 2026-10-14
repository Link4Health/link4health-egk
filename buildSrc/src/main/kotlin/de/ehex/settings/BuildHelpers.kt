package de.ehex.settings

import org.gradle.api.Project

private fun Project.requiredStringProperty(name: String): String =
    findProperty(name)?.toString() ?: error("Missing Gradle property: $name")

fun Project.currentBuildNumber(): String =
    if (GITHUB_BUILD_NUMBER != "0") {
        GITHUB_BUILD_NUMBER
    } else {
        requiredStringProperty("buildNumber")
    }

fun Project.egkLibraryPackageName(): String = requiredStringProperty("libraryPackageNameEgkAndroid")

fun Project.egkLibraryVersion(): String = requiredStringProperty("sdkVersion")

fun Project.optionalStringProperty(name: String): String? = findProperty(name)?.toString()
