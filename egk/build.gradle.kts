import de.ehex.settings.GITHUB_BUILD_NUMBER
import de.ehex.settings.getGitHash
import java.util.*

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.dokka.documentation)
    id("jacoco")
    `maven-publish`
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    api(libs.bcprov.jdk18on)
    api(libs.bcpkix.jdk18on)

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
