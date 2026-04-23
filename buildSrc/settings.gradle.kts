enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        val contextUrl = "https://nexus.link4.health/repository"
        val anonymousAccess = providers.gradleProperty("anonymous").orNull?.toBoolean() ?: true
        val nexusUsername = providers.gradleProperty("nexusUsername").orNull
        val nexusPassword = providers.gradleProperty("nexusPassword").orNull
        if (anonymousAccess) {
            println("using anonymous access")
            maven {
                name = "Link4Health Nexus"
                url = uri("$contextUrl/link4health-anonymous/")
                mavenContent {
                    releasesOnly()
                }
            }
        } else {
            println("using login access")
            maven {
                name = "Link4Health Nexus"
                url = uri("$contextUrl/link4health-development/")
                credentials {
                    username = nexusUsername
                    password = nexusPassword
                }
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        val contextUrl = "https://nexus.link4.health/repository"
        val anonymousAccess = providers.gradleProperty("anonymous").orNull?.toBoolean() ?: true
        val nexusUsername = providers.gradleProperty("nexusUsername").orNull
        val nexusPassword = providers.gradleProperty("nexusPassword").orNull
        if (anonymousAccess) {
            println("using anonymous access")
            mavenLocal()
            maven {
                name = "Link4Health Nexus"
                url = uri("$contextUrl/link4health-anonymous/")
                mavenContent {
                    releasesOnly()
                }
            }
        } else {
            println("using login access")
            mavenLocal()
            maven {
                name = "Link4Health Nexus"
                url = uri("$contextUrl/link4health-development/")
                credentials {
                    username = nexusUsername
                    password = nexusPassword
                }
            }
        }
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "buildSrc"
