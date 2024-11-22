import de.ehex.settings.GITHUB_BUILD_NUMBER
import de.ehex.settings.getGitHash
import de.ehex.settings.nameSpace
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.dokka.documentation)
    alias(libs.plugins.dedekt)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.dependency.check.gradle)
    alias(libs.plugins.gradle.license.report)
    alias(libs.plugins.spotless)
    jacoco
    `maven-publish`
}

apply(from = "../generateDoku.gradle.kts")

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "src/androidUnitTest/**/*.kt", "src/test/**/*.kt")
        ktlint("0.48.0").editorConfigOverride(
            mapOf(
                "indent_size" to "4",
                "continuation_indent_size" to "4",
                "max_line_length" to "180",
                "insert_final_newline" to "true",
                "charset" to "utf-8",
                "end_of_line" to "lf",
            ),
        )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("0.48.0").editorConfigOverride(
            mapOf(
                "indent_size" to "4",
                "continuation_indent_size" to "4",
                "max_line_length" to "180",
            ),
        )
    }
}

detekt {
    val configDir = "${project.layout.projectDirectory.asFile.absolutePath}/config/detekt"
    toolVersion = libs.versions.detekt.get()
    config.setFrom(files("$configDir/detekt.yml"))
    // Optional: Define which directories should be scanned. If unspecified, detekt will scan all kotlin sources.
    source.from(
        files(
            "src/main/kotlin",
            "src/main/java",
        ),
    )
    // Build upon the default configuration provided by Detekt
    buildUponDefaultConfig = true
    // Optional: By default, Detekt does not fail the build when issues are found. Set this to true to fail the build.
    ignoreFailures = false
    // Optional: If set to true, ignores all rules in the baseline XML.
    baseline = file("$configDir/detekt-baseline.xml")
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.withType<Test> {
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    dependsOn("testDebugUnitTest") // Ensure the report is generated after tests run

    reports {
        xml.required.set(false)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "android/**/*.*",
    )
    val mainSrc = "${project.projectDir}/src/main/java"

    val javaClasses = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
        exclude(fileFilter)
    }

    val kotlinClasses = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        exclude(fileFilter)
    }

    val combinedClasses = files(javaClasses, kotlinClasses)

    classDirectories.setFrom(combinedClasses)
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(fileTree(layout.buildDirectory).include("jacoco/testDebugUnitTest.exec"))
}

val nvdApiKey: String? by extra
dependencyCheck {
    autoUpdate = true
    analyzedTypes = listOf("jar, aar")
    format = "HTML"
    nvd.apiKey = nvdApiKey
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

licenseReport {
    // By default, this plugin will collect the union of all licenses from
    // the immediate pom and the parent poms. If your legal team thinks this
    // is too liberal, you can restrict collected licenses to only include the
    // those found in the immediate pom file
    // Defaults to: true
    unionParentPomLicenses = false

    // Set output directory for the report data.
    // Defaults to ${project.buildDir}/reports/dependency-license.
//    outputDir = "${rootProject.projectDir}/docs/licenses"

    // Select projects to examine for dependencies.
    // Defaults to current project and all its subprojects
    projects = arrayOf(project) + project.subprojects

    // Adjust the configurations to fetch dependencies. Default is 'runtimeClasspath'
    // For Android projects use 'releaseRuntimeClasspath' or 'yourFlavorNameReleaseRuntimeClasspath'
    // Use 'ALL' to dynamically resolve all configurations:
    // configurations = ALL
    configurations = arrayOf("releaseRuntimeClasspath")

    // Don't include artifacts of project's own group into the report
    excludeOwnGroup = true

    // Don't exclude bom dependencies.
    // If set to true, then all boms will be excluded from the report
    excludeBoms = false

    // This is for the allowed-licenses-file in checkLicense Task
    // Accepts File, URL or String path to local or remote file
    allowedLicensesFile = project.layout.projectDirectory.file("config/allowed-licenses.json").asFile
}

val injectVariable: MutableMap<String, String> = System.getenv()
val buildNumber: String = if (GITHUB_BUILD_NUMBER != "0") {
    GITHUB_BUILD_NUMBER
} else {
    project.extra["buildNumber"] as String
}
val libraryPackageNameEgkAndroid: String by project.extra
val majorEgkAndroid: String by project.extra
val minorEgkAndroid: String by project.extra
val patchEgkAndroid: String by project.extra

val libraryVersionEgk = "$majorEgkAndroid.$minorEgkAndroid.$patchEgkAndroid"
val gitHash = getGitHash()

android {
    namespace = nameSpace(project)
    compileSdk = libs.versions.compileSdk.get().toInt()
    lint {
        baseline = file("config/lint/lint-baseline.xml")
        abortOnError = true
        warningsAsErrors = true
        checkDependencies = true
    }
    defaultConfig {
        minSdk = libs.versions.minSDK.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    tasks.withType<com.android.build.gradle.tasks.BundleAar>().configureEach {
        val isDebugBuild = gradle.startParameter.taskNames.any { it.contains("assembleDebug") }
        val buildTypeSuffix = if (isDebugBuild) "SNAPSHOT" else ""
        val type = if (isDebugBuild) {
            "$libraryVersionEgk-$buildNumber-$gitHash-$buildTypeSuffix"
        } else {
            "$libraryVersionEgk-$buildNumber-$gitHash"
        }
        archiveFileName.set("${rootProject.name}-$type.aar")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.javaTarget.get()
    }
}

dependencies {

    implementation(libs.snakeyaml)
    implementation(libs.kotlinx.coroutines.core)
    api(libs.bcprov.jdk18on)
    api(libs.bcpkix.jdk18on)

    api(libs.napier)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        create<MavenPublication>("Link4HealthEgkLibrary") {
            groupId = libraryPackageNameEgkAndroid
            artifactId = "${rootProject.name}-library"
            version = if (isSnapshot()) {
                "$libraryVersionEgk-$buildNumber-SNAPSHOT"
            } else {
                libraryVersionEgk
            }
            val artifactFileName = if (isSnapshot()) {
                "${rootProject.name}-$libraryVersionEgk-$buildNumber-$gitHash-SNAPSHOT.aar"
            } else {
                "${rootProject.name}-$libraryVersionEgk-$buildNumber-$gitHash.aar"
            }

            artifact("${layout.buildDirectory.get()}/outputs/aar/$artifactFileName") {
                extension = "aar"
                classifier = gitHash
            }

            // Artefakte für JavaDoc und HTML-Dokumentation
            artifact(tasks.named("dokkaJavadocJar"))
            artifact(tasks.named("dokkaHtmlJar"))

            pom {
                name.set("Link4Health eGK Library")
                description.set("A library to use the egk for CardLink.")
                withXml {
                    asNode().appendNode("dependencies").apply {
                        configurations
                            .getByName("api")
                            .allDependencies
                            .forEach { dependency ->
                                appendNode("dependency").apply {
                                    appendNode("groupId", dependency.group)
                                    appendNode("artifactId", dependency.name)
                                    appendNode("version", dependency.version)
                                }
                            }
                    }
                }
                licenses {
                    license {
                        name.set("EUPL License")
                        url.set("https://joinup.ec.europa.eu/software/page/eupl")
                        distribution.set("repo")
                    }
                }
            }
        }
    }
    val contextUrl: String? by extra
    val nexusUsername: String? by extra
    val nexusPassword: String? by extra
    repositories {
        maven {
            name = "Link4HealthNexus"
            url = if (isSnapshot()) {
                uri("$contextUrl/link4health-snapshots")
            } else {
                uri("$contextUrl/link4health-releases")
            }
            credentials {
                username = nexusUsername
                password = nexusPassword
            }
        }
    }
}

fun isSnapshot(): Boolean {
    val aarDir = File("${layout.buildDirectory.get()}/outputs/aar")
    if (aarDir.exists() && aarDir.isDirectory) {
        val files = aarDir.listFiles { _, name -> name.endsWith("SNAPSHOT.aar") }
        return files?.isNotEmpty() ?: false
    }
    return false
}

val apiContextUrl: String? by extra
val nexusUsername: String? by extra
val nexusPassword: String? by extra
tasks.register("checkAndDeleteFolder") {
    group = "publishing"
    description = "Checks if the folder containing the artifact exists in the Nexus repository and deletes it if found."

    doLast {
        val repositoryUrl = "$apiContextUrl/service/rest/v1/components"
        val groupPath = "de.link4health.egk.api"
        val artifactName = "${rootProject.name}-library"
        val artifactVersion = libraryVersionEgk
        val queryUrlString = "$repositoryUrl?repository=link4health-releases"
        val auth = Base64.getEncoder().encodeToString("$nexusUsername:$nexusPassword".toByteArray())

        println("Checking if component $groupPath:$artifactName:$artifactVersion exists in Nexus repository.")

        var continuationToken: String? = null

        fun getComponents(token: String?): String? {
            return try {
                val url = if (token == null) {
                    URL(queryUrlString)
                } else {
                    URL("$queryUrlString&continuationToken=$token")
                }
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Basic $auth")

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    }

                    500 -> {
                        println("Component not found, response code: ${connection.responseCode}. No action needed.")
                        null
                    }

                    else -> {
                        println("Failed to check component in Nexus repository. HTTP response code: ${connection.responseCode}")
                        null
                    }
                }
            } catch (e: MalformedURLException) {
                println("The URL is malformed: ${e.message}")
                null
            } catch (e: IOException) {
                println("An I/O error occurred: ${e.message}")
                null
            }
        }

        fun deleteComponent(repositoryUrl: String, id: String, auth: String) {
            val deleteUrlString = "$repositoryUrl/$id"
            val deleteUrl = URL(deleteUrlString)

            val deleteConnection = deleteUrl.openConnection() as HttpURLConnection
            deleteConnection.requestMethod = "DELETE"
            deleteConnection.setRequestProperty("Authorization", "Basic $auth")
            val deleteResponseCode = deleteConnection.responseCode

            println("Delete request response code: $deleteResponseCode")

            when (deleteResponseCode) {
                HttpURLConnection.HTTP_NO_CONTENT, HttpURLConnection.HTTP_OK -> {
                    println("Component $groupPath:$artifactName:$artifactVersion deleted successfully from Nexus repository.")
                }

                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    println("Failed to delete component $groupPath:$artifactName:$artifactVersion from Nexus repository. Unauthorized: HTTP response code: $deleteResponseCode")
                }

                else -> {
                    println("Failed to delete component $groupPath:$artifactName:$artifactVersion from Nexus repository. HTTP response code: $deleteResponseCode")
                }
            }
        }

        var componentDeleted = false

        while (!componentDeleted) {
            val response = getComponents(continuationToken) ?: break

            // Parse JSON response
            val jsonResponse = JSONObject(response)
            val items = jsonResponse.getJSONArray("items")
            continuationToken = jsonResponse.optString("continuationToken", null)

            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val group = item.getString("group")
                val name = item.getString("name")
                val version = item.getString("version")
                val id = item.getString("id")

                if (group == groupPath && name == artifactName && version == artifactVersion) {
                    println("Component $groupPath:$artifactName:$artifactVersion exists. Proceeding with deletion of component ID: $id")
                    deleteComponent(repositoryUrl, id, auth)
                    componentDeleted = true
                    break
                }
            }

            if (continuationToken == null) {
                break
            }
        }

        if (!componentDeleted) {
            println("Component $groupPath:$artifactName:$artifactVersion does not exist in Nexus repository. Nothing to delete.")
        }
    }
}

tasks.named("publishLink4HealthEgkLibraryPublicationToLink4HealthNexusRepository") {
    dependsOn(tasks.named("checkAndDeleteFolder"))
}
