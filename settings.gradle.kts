pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PodcastApp"

include(
    ":app",
    ":core:ui",
    ":core:network",
    ":core:database",
    ":core:datastore",
    ":core:media",
    ":core:data",
    ":core:player",
    ":feature:podcast",
    ":feature:episode",
    ":feature:download",
)
