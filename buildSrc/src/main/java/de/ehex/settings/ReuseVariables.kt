package de.ehex.settings

import org.gradle.api.Project
import java.nio.file.Paths
import java.util.*

val userHome: String = System.getProperty("user.home")
val injectVariable: Map<String, String> = System.getenv()
val GITHUB_BUILD_NUMBER: String = System.getenv("GITHUB_RUN_NUMBER") ?: "0"

/**
 * Loads the user-specific Gradle properties from the ".gradle
 **/
fun loadUserGradleProperties(): Properties {
    val propertiesFile = Paths.get(userHome, ".gradle", "gradle.properties").toFile()

    return Properties().apply {
        if (propertiesFile.exists()) {
            propertiesFile.reader().use { reader -> load(reader) }
        }
    }
}

/**
 * Retrieves the abbreviated hash of the current Git commit.
 *
 * @return The abbreviated Git hash of the current commit.
 */
fun Project.getGitHash(): String {
    val dir = project.projectDir
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
        .directory(dir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    return process.inputStream.bufferedReader().readText().trim()
}
