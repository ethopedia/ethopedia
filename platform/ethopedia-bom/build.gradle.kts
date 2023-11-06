plugins {
    `java-platform`
    `maven-publish`
    id("me.qoomon.git-versioning") version "6.3.6"
}

group = "org.ethopedia"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        rootProject.subprojects.forEach { project ->
            val projectName = project.displayName
                .replace("project '", "")
                .replace("'", "")

            if (projectName.startsWith(":packages")) {
                api(project(projectName))
            }
        }
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("ethopedia-bom") {
            from(components["javaPlatform"])
        }
    }
}
