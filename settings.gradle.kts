enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        val anonymous: String? by extra
        val contextUrl: String? by extra
        val nexusUsername: String? by extra
        val nexusPassword: String? by extra
        val anonymousAccess = anonymous?.toBoolean() ?: true
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
        val anonymous: String? by extra
        val contextUrl: String? by extra
        val nexusUsername: String? by extra
        val nexusPassword: String? by extra
        val anonymousAccess = anonymous?.toBoolean() ?: true
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
