package de.ehex.settings

import org.gradle.api.Project
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*

val injectVariable: Map<String, String> = System.getenv()
val GITHUB_BUILD_NUMBER: String = System.getenv("GITHUB_RUN_NUMBER") ?: "0"

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
