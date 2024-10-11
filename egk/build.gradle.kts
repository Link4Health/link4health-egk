import de.ehex.settings.GITHUB_BUILD_NUMBER
import de.ehex.settings.doesArtifactExist
import de.ehex.settings.getGitHash
import java.net.URISyntaxException

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
    // By default this plugin will collect the union of all licenses from
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
val buildNumber: String = GITHUB_BUILD_NUMBER
val libraryPackageNameEgkAndroid: String = libs.versions.libraryPackageNameEgkAndroid.get()
val majorVersion = libs.versions.majorEgkAndroid.get()
val minorVersion = libs.versions.minorEgkAndroid.get()
val patchVersion = libs.versions.patchEgkAndroid.get()
val releaseType: String by project.extra

val libraryVersion = "$majorVersion.$minorVersion.$patchVersion"
val gitHash = getGitHash()

android {
    namespace = libs.versions.nameSpaceEgkiAndroid.get()
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
        val type = if (releaseType.isEmpty()) {
            libraryVersion
        } else {
            "$libraryVersion-$gitHash-$releaseType"
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
            artifactId = rootProject.name
            version = if (releaseType.isEmpty()) {
                libraryVersion
            } else {
                "$libraryVersion-$gitHash-$releaseType"
            }
            artifact("${layout.buildDirectory.get()}/outputs/aar/$artifactId-$version.aar") {
                extension = "aar"
            }
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
    val gitHubContextUrl: String? by extra
    val githubUser: String? by extra
    val githubToken: String? by extra
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("$gitHubContextUrl/Link4Health/link4health-egk-library")
            credentials {
                username = githubUser
                password = githubToken
            }
        }
    }
}

afterEvaluate {
    tasks.named("publishLink4HealthEgkLibraryPublicationToMavenLocal") {
        dependsOn("clean")
        mustRunAfter("clean")
        dependsOn("bundleReleaseAar")
    }
    tasks.named("publishLink4HealthEgkLibraryPublicationToGitHubPackagesRepository") {
        dependsOn("clean")
        mustRunAfter("clean")
        dependsOn("bundleReleaseAar")
        dependsOn("checkEgkExistence") // Ensures `checkEgkExistence` runs before this task
        onlyIf {
            val artifactEgkExists = project.extra["artifactEgkExists"] as? Boolean ?: false
            !artifactEgkExists
        }
        doFirst {
            println("Publishing Link4HealthSharedApi as the artifact does not exist.")
        }
    }
    tasks.named("bundleReleaseAar") {
        mustRunAfter("clean")
    }
}

tasks.register("checkEgkExistence") {
    group = "verification"
    doLast {
        val repositoryUrl = "https://maven.pkg.github.com/Link4Health/link4health-egk-library"
        val groupId = "de/link4health/egk/api"
        val artifactId = "link4health-egk-library"
        val version = if (releaseType.isEmpty()) {
            "$libraryVersion-$gitHash"
        } else {
            "$libraryVersion-$gitHash-$releaseType"
        }

        val artifactPath = "$groupId/$artifactId/$version/$artifactId-$version.aar"

        val username = project.findProperty("githubUser") as String
        val token = project.findProperty("githubToken") as String

        try {
            println("Checking the existence of the artifact at: $repositoryUrl/$artifactPath")
            val artifactEgkExists = doesArtifactExist(repositoryUrl, artifactPath, username, token)
            project.extra["artifactEgkExists"] = artifactEgkExists
            if (artifactEgkExists) {
                println("The artifact already exists.")
            } else {
                println("The artifact does not exist.")
            }
        } catch (e: URISyntaxException) {
            println("URISyntaxException: ${e.message}")
        }
    }
}
