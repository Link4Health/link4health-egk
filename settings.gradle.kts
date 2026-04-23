enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        val anonymousAccess = providers.gradleProperty("anonymous").orNull?.toBoolean() ?: true
        val contextUrl = providers.gradleProperty("contextUrl").orNull
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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        val anonymousAccess = providers.gradleProperty("anonymous").orNull?.toBoolean() ?: true
        val contextUrl = providers.gradleProperty("contextUrl").orNull
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
}
rootProject.name = "link4health-egk"
include(":egk")
