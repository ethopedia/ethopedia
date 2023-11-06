import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("org.ethopedia.platform.conventions")
    id("com.expediagroup.graphql") version "7.0.1"
    id("com.bmuschko.docker-java-application") version "9.3.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.ethopedia.platform"

application {
    mainClass.set("org.ethopedia.apps.api.MainKt")
}

shadow {}

//repositories {
//    mavenCentral()
//    maven {
//        url = uri("https://maven.pkg.github.com/Ethopedia/monorepo")
//        credentials {
//            username = project.findProperty("ethopedia.gpr.user")?.toString() ?: System.getenv("GH_USER") ?: System.getenv("GITHUB_USER")
//            password = project.findProperty("ethopedia.gpr.token")?.toString() ?: System.getenv("GH_TOKEN") ?: System.getenv("GITHUB_TOKEN")
//        }
//    }
//}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // implementation(libs.kotlinx.coroutines)

    implementation(libs.logback.core)
    implementation(libs.logback.classic)

    implementation(libs.jackson.kotlin)

    implementation(platform(libs.aws.sdk.bom))
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.0")

    implementation(libs.ktor.contentnegotiation)
    implementation(libs.ktor.json)
    implementation(libs.ktor.netty)

    implementation("com.expediagroup:graphql-kotlin-server:7.0.1")
    implementation("com.expediagroup:graphql-kotlin-ktor-server:7.0.1")

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
}

docker {
    javaApplication {
        baseImage.set("amazoncorretto:17-alpine")
        ports.set(listOf(8080))
    }
}


java {
    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}


tasks {
    kotlin {
        jvmToolchain(17)
    }
    test {
        useJUnitPlatform()
    }
}
