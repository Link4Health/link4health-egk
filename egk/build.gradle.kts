import de.ehex.settings.currentBuildNumber
import de.ehex.settings.egkLibraryPackageName
import de.ehex.settings.egkLibraryVersion
import de.ehex.settings.getGitHash
import de.ehex.settings.isSnapshotArtifactBuild
import de.ehex.settings.nameSpace
import de.ehex.settings.optionalStringProperty
import de.ehex.settings.registerDocumentationTasks

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.dokka.documentation)
    alias(libs.plugins.detekt)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.dependency.check.gradle)
    alias(libs.plugins.gradle.license.report)
    alias(libs.plugins.spotless)
    jacoco
    `maven-publish`
}

registerDocumentationTasks()

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

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "21"
}
tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "21"
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

dependencyCheck {
    autoUpdate = true
    analyzedTypes = listOf("jar, aar")
    format = "HTML"
    nvd.apiKey = project.optionalStringProperty("nvdApiKey")
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
    // outputDir = "${rootProject.projectDir}/docs/licenses"

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

android {
    namespace = nameSpace(project)
    compileSdk = libs.versions.compileSdk.get().toInt()
    lint {
        abortOnError = true
        warningsAsErrors = false
        checkDependencies = true
        targetSdk = libs.versions.targetSdk.get().toInt()
        lintConfig = rootProject.file("lint.xml")
        disable += setOf(
            "AndroidGradlePluginVersion",
            "GradleDependency",
            "NewerVersionAvailable",
            "OldTargetApi",
        )
    }
    defaultConfig {
        minSdk = libs.versions.minSDK.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    tasks.withType<com.android.build.gradle.tasks.BundleAar>().configureEach {
        val libraryVersionEgk = project.egkLibraryVersion()
        val buildNumber = project.currentBuildNumber()
        val gitHash = project.getGitHash()
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.javaTarget.get()))
    }
}

dependencies {

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
    val googleArtifactRegistryUrl: String? by extra
    val googleArtifactRegistryAccessToken: String? by extra
    repositories {
        if (!googleArtifactRegistryUrl.isNullOrBlank()) {
            maven {
                name = "GoogleArtifactRegistry"
                url = uri(googleArtifactRegistryUrl as String)
                credentials {
                    // Google Artifact Registry accepts an OAuth2 access token as a basic auth password
                    // with this fixed username. See https://cloud.google.com/artifact-registry/docs/java/authentication
                    username = "oauth2accesstoken"
                    password = googleArtifactRegistryAccessToken
                }
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("Link4HealthEgkLibrary") {
                val libraryVersionEgk = project.egkLibraryVersion()
                groupId = project.egkLibraryPackageName()
                artifactId = "${rootProject.name}-library"
                version = if (project.isSnapshotArtifactBuild()) {
                    "$libraryVersionEgk-${project.currentBuildNumber()}-SNAPSHOT"
                } else {
                    libraryVersionEgk
                }
                from(components["release"])
                artifact(tasks.named("dokkaJavadocJar"))
                artifact(tasks.named("dokkaHtmlJar"))
                pom {
                    name.set("Link4Health eGK Library")
                    description.set("Android library for communicating with electronic health cards.")
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
    }
}
