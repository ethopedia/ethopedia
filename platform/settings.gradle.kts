pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "platform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":packages")

include(":apps")
include(":apps:api")

include(":ethopedia-bom")

