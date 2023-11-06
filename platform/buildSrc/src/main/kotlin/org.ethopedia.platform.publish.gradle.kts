import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

configure<PublishingExtension> {
    publications {
        create<MavenPublication>(name) {
            from(components["java"])
        }
    }
}
