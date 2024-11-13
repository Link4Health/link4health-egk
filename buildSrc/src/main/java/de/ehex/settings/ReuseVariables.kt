package de.ehex.settings

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*

val injectVariable: Map<String, String> = System.getenv()
val GITHUB_BUILD_NUMBER: String = System.getenv("GITHUB_RUN_NUMBER") ?: "0"

const val MISSING_GRADLE_PROPERTIES = "Missing entry nameSpace in gradle.properties"
const val GRADLE_PROPERTIES_NOT_FOUND = "Missing gradle.properties file"
const val CREATE_GRADLE_PROPERTIES_IN_DIRECTORY = "Create gradle.properties in project root directory"

fun nameSpace(target: Project): String {
    val file = File("${target.project.projectDir}")
    val gradleProperties = File("${file.parent}/gradle.properties")
    return if (gradleProperties.canRead()) {
        val props = Properties()
        props.load(FileInputStream(gradleProperties))
        if (props.containsKey("nameSpace")) {
            props["nameSpace"] as String
        } else {
            println(MISSING_GRADLE_PROPERTIES)
            ""
        }
    } else {
        println(GRADLE_PROPERTIES_NOT_FOUND)
        println(CREATE_GRADLE_PROPERTIES_IN_DIRECTORY + file.parent)
        ""
    }
}

fun Project.getGitHash(): String {
    val dir = project.projectDir
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
        .directory(dir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    return process.inputStream.bufferedReader().readText().trim()
}

// To avoid changing the original order, the following function and task are placed here:
fun doesArtifactExist(repositoryUrl: String, artifactPath: String, username: String?, token: String?): Boolean {
    if (username == null || token == null) {
        println("Username and token must not be null.")
        return false
    }

    val urlString = "$repositoryUrl/$artifactPath".replace(" ", "%20")
    val url: URL? = try {
        URI(urlString).toURL()
    } catch (e: URISyntaxException) {
        println("URISyntaxException: ${e.message}")
        null
    }

    return if (url != null) {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "HEAD"
            setRequestProperty(
                "Authorization",
                "Basic " + Base64.getEncoder().encodeToString("$username:$token".toByteArray())
            )
            responseCode == HttpURLConnection.HTTP_OK
        }
    } else {
        false
    }
}
