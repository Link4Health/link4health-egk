package de.ehex.settings

import org.gradle.api.Project
import java.io.File

private fun Project.requiredStringProperty(name: String): String =
    findProperty(name)?.toString() ?: error("Missing Gradle property: $name")

fun Project.currentBuildNumber(): String =
    if (GITHUB_BUILD_NUMBER != "0") {
        GITHUB_BUILD_NUMBER
    } else {
        requiredStringProperty("buildNumber")
    }

fun Project.egkLibraryPackageName(): String = requiredStringProperty("libraryPackageNameEgkAndroid")

fun Project.egkLibraryVersion(): String = listOf(
    requiredStringProperty("majorEgkAndroid"),
    requiredStringProperty("minorEgkAndroid"),
    requiredStringProperty("patchEgkAndroid"),
).joinToString(".")

fun Project.optionalStringProperty(name: String): String? = findProperty(name)?.toString()

fun Project.isSnapshotArtifactBuild(): Boolean {
    val aarDir = File("${layout.buildDirectory.get()}/outputs/aar")
    if (!aarDir.exists() || !aarDir.isDirectory) {
        return false
    }

    val files = aarDir.listFiles { _, fileName -> fileName.endsWith("SNAPSHOT.aar") }
    return files?.isNotEmpty() ?: false
}
