plugins {
    `maven-publish`
    id("me.qoomon.git-versioning") version "6.3.6"
    id("com.github.ben-manes.versions") version "0.41.0"
    id("nl.littlerobots.version-catalog-update") version "0.8.1"
}

group = "org.ethopedia"

gitVersioning.apply {
    refs {
        branch(".+") {
            version = "\${ref}-SNAPSHOT"
        }
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }

    // optional fallback configuration in case of no matching ref configuration
    rev {
        version = "\${commit}"
    }
}

fun RepositoryHandler.githubPackagesRepository() {
    maven {
        name = "GithubPackages"
        url = uri("https://maven.pkg.github.com/Ethopedia/monorepo")
        credentials {
            username = project.findProperty("ethopedia.gpr.user")?.toString() ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("ethopedia.gpr.token")?.toString() ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "maven-publish")

    configure<PublishingExtension> {
        repositories {
            githubPackagesRepository()
        }
    }
}

versionCatalogUpdate {
    // sort the catalog by key (default is true)
    sortByKey.set(false)

    // References that are pinned are not automatically updated.
    // They are also not automatically kept however (use keep for that).
    keep {
        // keep versions without any library or plugin reference
        keepUnusedVersions.set(true)
        // keep all libraries that aren't used in the project
        keepUnusedLibraries.set(true)
        // keep all plugins that aren't used in the project
        keepUnusedPlugins.set(true)
    }
}
