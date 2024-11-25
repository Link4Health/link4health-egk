enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        val contextUrl = "https://nexus.link4.health/repository"
        val nexusUsername: String? by extra
        val nexusPassword: String? by extra
        val anonymous: String? by extra
        val anonymousAccess = anonymous?.toBoolean() ?: true
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
        val contextUrl = "https://nexus.link4.health/repository"
        val nexusUsername: String? by extra
        val nexusPassword: String? by extra
        val anonymous: String? by extra
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
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "buildSrc"
