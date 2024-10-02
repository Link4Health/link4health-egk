import java.io.File
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
            println("Überprüfung ob 'pandoc' installiert ist:")
            val pandocVersionOutput = runCommand(pandoc, "--version")
            println("Pandoc Version: $pandocVersionOutput")

            println("Readme File: ${readmeFile.absolutePath}")
            println("Release Notes File: ${releaseNotesFile.absolutePath}")
            println("Template File: ${templateFile.absolutePath}")
            println("Output File: ${outputFile.absolutePath}")

            var readmeContent = readmeFile.readText()
            var releaseNotesContent = if (releaseNotesFile.exists()) releaseNotesFile.readText() else ""

            // Anpassung des Markdown-Inhalts zur Unterstützung benutzerdefinierter Annotationen
            readmeContent = processCustomAnnotations(readmeContent)
            releaseNotesContent = processCustomAnnotations(releaseNotesContent)

            val readmeHtml = runCommandWithInput(input = readmeContent, pandoc, "-f", "markdown", "-t", "html")
            val processedReadmeHtml = processHtml(readmeHtml)
            println("Generated README HTML: $processedReadmeHtml")

            val releaseNotesHtml = if (releaseNotesContent.isNotEmpty()) {
                runCommandWithInput(input = releaseNotesContent, pandoc, "-f", "markdown", "-t", "html")
            } else {
                println("RELEASENOTES.md file does not exist!")
                ""
            }
            val processedReleaseNotesHtml = processHtml(releaseNotesHtml)
            println("Generated Release Notes HTML: $processedReleaseNotesHtml")

            val versionTag = getLatestGitTag()
            println("Version Tag: $versionTag")

            val versionDirectory = File(outputDirectory, "releases/$versionTag")
            versionDirectory.mkdirs() // Ensure the directory is created
            println("Version Directory: ${versionDirectory.absolutePath}")

            // Debugging-Ausgaben für den Kopiervorgang
            println("Quelle der JavaDoc-Dateien: build/dokka/html")
            println("Ziel für die JavaDoc-Dateien: $versionDirectory/javaDoc")
            copyFiles("egk/build/dokka/html", "$versionDirectory/javaDoc")

            println("Quelle der Lizenzberichte: egk/build/reports/dependency-license")
            println("Ziel für die Lizenzberichte: $versionDirectory/licenses")
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

            var existingDocument = if (outputFile.exists()) {
                println("Output file exists, reading its content.")
                outputFile.readText()
            } else {
                println("Using the template file as output file does not exist.")
                templateFile.readText()
            }

            println("Initial Content of index.html before replacement:\n$existingDocument")

            // Check if the documentation for this tag already exists
            if (!existingDocument.contains(versionSection)) {
                existingDocument = existingDocument.replace("<!-- README_CONTENT_PLACEHOLDER -->", processedReadmeHtml)
                existingDocument =
                    existingDocument.replace("<!-- DOCUMENTATION_SECTIONS_PLACEHOLDER -->", "$versionSection\n<!-- DOCUMENTATION_SECTIONS_PLACEHOLDER -->")
            } else {
                println("Documentation for version $versionTag already exists in index.html")
            }

            println("Final Content of index.html before writing:\n$existingDocument")

            if (!outputFile.exists()) {
                println("Output file does not exist. It will be created.")
            }

            // Writing final content to index.html
            outputFile.writeText(existingDocument)
            println("Updated index.html file written at: ${outputFile.absolutePath}")

            // Speichern der Release-Notes HTML Datei
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
                    $processedReleaseNotesHtml
                </div>
                </body>
                </html>
            """.trimIndent()
            releaseNotesFullPath.writeText(releaseNotesHtmlFull)
            println("Release notes HTML file written at: ${releaseNotesFullPath.absolutePath}")

        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
            e.printStackTrace()
        }
    }
}

// Methode zur Anpassung des generierten HTMLs
fun processHtml(html: String): String {
    // Beispiel: Hinzufügen der Klasse "note" zu bestimmten Blockquotes
    val blockquoteNotePattern = Regex("""<blockquote>\s*<p>\[!CAUTION\](.*?)<\/p>\s*<\/blockquote>""", RegexOption.DOT_MATCHES_ALL)
    return blockquoteNotePattern.replace(html) {
        """<blockquote class="caution"><p>${it.groupValues[1].trim()}</p></blockquote>"""
    }
}

// Methode zur Anpassung benutzerdefinierter Annotationen im Markdown-Inhalt
fun processCustomAnnotations(markdown: String): String {
    val annotationPatterns = listOf(
        "CAUTION" to "caution",
        "INFO" to "info"
    )
    var processedMarkdown = markdown
    for ((annotation, cssClass) in annotationPatterns) {
        val pattern = Regex("""\s*>\s*\[!$annotation]\s*\n>\s*(.*?)\s*\n""", RegexOption.DOT_MATCHES_ALL)
        processedMarkdown = pattern.replace(processedMarkdown) {
            """<blockquote class="$cssClass"><p>${it.groupValues[1].trim()}</p></blockquote>"""
        }
    }
    return processedMarkdown
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

fun getLatestGitTag(): String {
    val tags = runCommand("git", "tag", "-l", "--sort=-v:refname").lines()
    if (tags.isNotEmpty()) {
        return tags.first().trim()
    } else {
        throw RuntimeException("Keine Tags im Repository gefunden!")
    }
}
