import java.io.File
import java.util.Properties
import kotlin.text.Regex

val pandoc = "pandoc"

tasks.register("generateDocIndexHtml") {
    group = "documentation"
    description = "Generates the index.html from README.md, RELEASENOTES.md and template."

    dependsOn("dokkaHtml")  // Abhängigkeit auf dokkaHtml
    dependsOn("generateLicenseReport")  // Abhängigkeit auf generateLicenseReport

    val readmeFile = file("../README.md")
    val releaseNotesFile = file("../RELEASENOTES.md")
    val templateFile = file("../docs/index_template.html")
    val outputDirectory = file("../docs")
    val outputFile = File(outputDirectory, "index.html")

    doLast {
        try {
            println("Checking if 'pandoc' is installed:")
            val pandocVersionOutput = runCommand(pandoc, "--version")
            println("Pandoc Version: $pandocVersionOutput")

            println("Readme File: ${readmeFile.absolutePath}")
            println("Release Notes File: ${releaseNotesFile.absolutePath}")
            println("Template File: ${templateFile.absolutePath}")
            println("Output File: ${outputFile.absolutePath}")

            val readmeContent = readmeFile.readText()
            val releaseNotesContent = if (releaseNotesFile.exists()) releaseNotesFile.readText() else ""

            val readmeHtml = runCommandWithInput(input = readmeContent, pandoc, "-f", "markdown", "-t", "html")

            val releaseNotesHtml = if (releaseNotesContent.isNotEmpty()) {
                runCommandWithInput(input = releaseNotesContent, pandoc, "-f", "markdown", "-t", "html")
            } else {
                println("RELEASENOTES.md file does not exist!")
                ""
            }

            val versionTag = "v${getVersionFromProperties()}"
            println("Version Tag: $versionTag")

            val versionDirectory = File(outputDirectory, "releases/$versionTag")
            versionDirectory.mkdirs() // Ensure the directory is created
            println("Version Directory: ${versionDirectory.absolutePath}")

            println("Source for JavaDoc files: build/dokka/html")
            println("Target for JavaDoc files: $versionDirectory/javaDoc")
            copyFiles("egk/build/dokka/html", "$versionDirectory/javaDoc")

            println("Source for license reports: egk/build/reports/dependency-license")
            println("Target for license reports: $versionDirectory/licenses")
            copyFiles("egk/build/reports/dependency-license", "$versionDirectory/licenses")

            val javadocPath = "releases/$versionTag/javaDoc/index.html"
            val licensesPath = "releases/$versionTag/licenses/index.html"
            val releaseNotesPath = "releases/$versionTag/release-notes.html"

            val javadocLink = "<a href=\"$javadocPath\">JavaDoc</a>"
            val licensesLink = "<a href=\"$licensesPath\">Licenses</a>"
            val releaseNotesLink = "<a href=\"$releaseNotesPath\">Release Notes</a>"

            val versionSection = """
                <div class="version-section">
                    <h4>Documentation for Version $versionTag</h4>
                    <ul>
                        <li>$javadocLink</li>
                        <li>$licensesLink</li>
                        <li>$releaseNotesLink</li>
                    </ul>
                </div>
            """.trimIndent()

            val templateContent = templateFile.readText()

            val existingIndexContent = if (outputFile.exists()) outputFile.readText() else ""
            val documentationVersionsRegex = Regex("""<div id="documentation-versions">(.*?)</div>""", RegexOption.DOT_MATCHES_ALL)
            val existingVersionsMatch = documentationVersionsRegex.find(existingIndexContent)
            val existingVersions = existingVersionsMatch?.groupValues?.get(1) ?: ""

            val newVersionSection = if (!existingVersions.contains(versionTag)) {
                "$versionSection\n"
            } else {
                ""
            }

            var updatedContent = templateContent.replace("<!-- README_CONTENT_PLACEHOLDER -->", readmeHtml)
            updatedContent = updatedContent.replace("<!-- DOCUMENTATION_SECTIONS_PLACEHOLDER -->", "$existingVersions$newVersionSection")

            println("Final Content of index.html before writing:\n$updatedContent")

            outputFile.writeText(updatedContent)
            println("Updated index.html file written to: ${outputFile.absolutePath}")

            val releaseNotesFullPath = File(outputDirectory, releaseNotesPath)
            println("Creating release notes directory if it doesn't exist: ${releaseNotesFullPath.parentFile}")
            releaseNotesFullPath.parentFile.mkdirs() // Ensure the parent directory is created

            val releaseNotesHtmlFull = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta content="width=device-width, initial-scale=1.0" name="viewport">
                    <title>Release Notes $versionTag</title>
                    <link href="../../style.css" rel="stylesheet">
                </head>
                <body>
                <div class="container">
                    <a href="../../index.html">Back to Documentation Overview</a>
                    <h1>Release Notes $versionTag</h1>
                    $releaseNotesHtml
                </div>
                </body>
                </html>
            """.trimIndent()
            releaseNotesFullPath.writeText(releaseNotesHtmlFull)
            println("Release notes HTML file written to: ${releaseNotesFullPath.absolutePath}")

        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
            e.printStackTrace()
        }
    }
}


// Globale Definition der Methode copyFiles
fun copyFiles(sourceDir: String, destDir: String) {
    val source = File(sourceDir)
    val destination = File(destDir)
    if (source.exists()) {
        source.copyRecursively(destination, true)
        println("Copied $sourceDir to $destDir")
    } else {
        println("Source directory $sourceDir does not exist!")
    }
}

fun runCommand(vararg command: String): String {
    val process = ProcessBuilder(*command).redirectErrorStream(true).start()
    return process.inputStream.bufferedReader().use { it.readText() }.trim()
}

fun runCommandWithInput(input: String, vararg command: String): String {
    val process = ProcessBuilder(*command)
        .redirectErrorStream(true)
        .start()
    process.outputStream.bufferedWriter().use { it.write(input) }
    return process.inputStream.bufferedReader().use { it.readText() }.trim()
}

fun getVersionFromProperties(): String {
    val propertiesFile = file("../gradle.properties")
    if (propertiesFile.exists()) {
        val properties = Properties()
        propertiesFile.inputStream().use { properties.load(it) }
        val major = properties.getProperty("majorEgkAndroid", "0")
        val minor = properties.getProperty("minorEgkAndroid", "0")
        val patch = properties.getProperty("patchEgkAndroid", "0")
        return "$major.$minor.$patch"
    }
    throw RuntimeException("gradle.properties file not found!")
}
