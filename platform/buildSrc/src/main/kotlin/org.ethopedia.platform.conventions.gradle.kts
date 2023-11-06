import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    // id("org.jlleitschuh.gradle.ktlint")
    // id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenCentral()
    mavenLocal {
        content {
            includeGroup("org.ethopedia")
        }
    }
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

    implementation("ch.qos.logback:logback-classic:1.4.8")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
}

group = "org.ethopedia.platform"

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
//    detekt {
//        toolVersion = "1.23.0"
//        config.from(files("${rootProject.projectDir}/detekt.yml"))
//    }
    test {
        useJUnitPlatform()
    }
}

tasks.withType<Test> {
    testLogging {
        events = setOf(
            TestLogEvent.PASSED,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.STANDARD_ERROR
        )
    }

    afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
        if (desc.parent == null) { // will match the outermost suite
            val pass = "${Color.GREEN}${result.successfulTestCount} passed${Color.NONE}"
            val fail = "${Color.RED}${result.failedTestCount} failed${Color.NONE}"
            val skip = "${Color.YELLOW}${result.skippedTestCount} skipped${Color.NONE}"
            val type = when (val r = result.resultType) {
                TestResult.ResultType.SUCCESS -> "${Color.GREEN}$r${Color.NONE}"
                TestResult.ResultType.FAILURE -> "${Color.RED}$r${Color.NONE}"
                TestResult.ResultType.SKIPPED -> "${Color.YELLOW}$r${Color.NONE}"
            }
            val output = "Results: $type (${result.testCount} tests, $pass, $fail, $skip)"
            val startItem = "|   "
            val endItem = "   |"
            val repeatLength = startItem.length + output.length + endItem.length - 36
            println("")
            println("\n" + ("-" * repeatLength) + "\n" + startItem + output + endItem + "\n" + ("-" * repeatLength))
        }
    }))
}

operator fun String.times(x: Int): String {
    return List(x) { this }.joinToString("")
}

internal enum class Color(ansiCode: Int) {
    NONE(0), BLACK(30), RED(31), GREEN(32), YELLOW(33), BLUE(34), PURPLE(35), CYAN(36), WHITE(37);

    private val ansiString: String = "\u001B[${ansiCode}m"

    override fun toString(): String {
        return ansiString
    }
}
