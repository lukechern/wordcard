pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repo.maven.apache.org/maven2/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven") }
    }
}

rootProject.name = "word card"
include(":app")
