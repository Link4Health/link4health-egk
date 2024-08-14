import de.ehex.settings.GITHUB_BUILD_NUMBER
import de.ehex.settings.getGitHash
import java.util.*

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.dokka.documentation)
    alias(libs.plugins.dedekt)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.dependency.check.gradle)
    alias(libs.plugins.gradle.license.report)
    jacoco
    `maven-publish`
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
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
    outputDir = "${rootProject.projectDir}/docs/licenses"

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
val libraryPackageName: String = libs.versions.libraryPackageNameEgkAndroid.get()
val libraryVersion =
    "${libs.versions.majorEgkAndroid.get()}.${libs.versions.minorEgkAndroid.get()}.${libs.versions.patchEgkAndroid.get()}"

android {
    namespace = libs.versions.nameSpaceEgkiAndroid.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

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
        val type = if (name.lowercase(Locale.getDefault()).contains("release")) {
            "$libraryVersion-${getGitHash()}"
        } else {
            "$libraryVersion-#$buildNumber-${getGitHash()}"
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
    api(libs.bcprov.jdk18on)
    api(libs.bcpkix.jdk18on)

    api(libs.napier)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        create<MavenPublication>("Link4HealthEgkLibrary") {
            groupId = libraryPackageName
            artifactId = rootProject.name
            version = libraryVersion
            artifact("${layout.buildDirectory.get()}/outputs/aar/$artifactId-$version-${getGitHash()}.aar") {
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
}

tasks.named("publishLink4HealthEgkLibraryPublicationToMavenLocal") {
    dependsOn(tasks.named("bundleReleaseAar"))
}
