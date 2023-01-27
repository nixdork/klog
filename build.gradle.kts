import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.concurrent.TimeUnit

group = "org.nixdork"
version = "0.0.1"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.0"
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.adarshr.test-logger") version "3.2.0"
    id("com.faire.gradle.analyze") version "1.0.9"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("com.diffplug.spotless") version "6.14.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // implementation("")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-core:2.14.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.1")
    implementation("com.zaxxer:HikariCP")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("javax.validation:validation-api:2.0.1.Final")
    //implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.jetbrains:annotations:13.0")
    implementation("org.postgresql:postgresql:42.5.1")
    //implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.0.1")
    implementation("org.springframework.boot:spring-boot:3.0.1")
    implementation("org.springframework:spring-context:6.0.3")
    implementation("org.springframework:spring-web:6.0.3")
    implementation("org.springframework:spring-webflux:6.0.3")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    //annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // testImplementation("")
    testImplementation("io.github.serpro69:kotlin-faker:1.13.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-framework-datatest:5.5.4")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.4")
    testImplementation("io.kotest:kotest-property:5.5.4")
    testImplementation("io.kotest:kotest-assertions-shared-jvm:5.5.4")
    testImplementation("io.kotest:kotest-framework-api-jvm:5.5.4")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.3.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.17.6")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:jdbc:1.17.6")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}

val jvmVersion = 17
kotlin { jvmToolchain(jvmVersion) }

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }

    all {
        if (isKotlinJvm) {
            resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
            resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
        }
    }
}

configure<DetektExtension> {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config = files("${rootDir.path}/detekt.yml")
    source = files("src/main/kotlin", "src/test/kotlin")
}

configure<SpotlessExtension> {
    kotlin {
        targetExclude("**/generated/**")
        ktlint().setUseExperimental(false)
    }
    kotlinGradle { ktlint().setUseExperimental(false) }
}

configure<TestLoggerExtension> {
    theme = ThemeType.STANDARD // project level
    showCauses = true
    showExceptions = true
    showFullStackTraces = false
    showPassed = true
    showSkipped = true
    showStackTraces = true
    showStandardStreams = false
    showSummary = true
    slowThreshold = 5000
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
            languageVersion = "1.8"
            apiVersion = "1.8"
            jvmTarget = jvmVersion.toString()
        }
    }

    withType<Test> {
        configureEach {
            maxParallelForks = 1
            useJUnitPlatform()
            // testLogging {
            //     setExceptionFormat("full")
            //     setEvents(listOf("passed", "skipped", "failed", "standardOut", "standardError"))
            // }
        }
    }

    withType<Detekt> {
        configureEach {
            jvmTarget = "17"
            reports {
                html.required.set(true) // observe findings in your browser with structure and code snippets
                xml.required.set(false) // checkstyle like format mainly for integrations like Jenkins
                txt.required.set(false) // similar to the console output, contains issue signature to manually edit baseline files
                sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
            }
        }
    }

    withType<DetektCreateBaselineTask> {
        configureEach {
            jvmTarget = "17"
        }
    }

    register("lint") { // runs "spotlessCheck"
        group = "verification"
        description = "Lint all code using configured linters. Runs 'spotlessCheck' "
        dependsOn(named("spotlessCheck"))
    }

    register("ktlint") { // runs "spotlessKotlinCheck" and "spotlessKotlinGradleCheck"
        group = "verification"
        description = "Lint Kotlin code. Runs 'spotlessKotlinCheck' and 'spotlessKotlinGradleCheck'"
        dependsOn(
            named("spotlessKotlinCheck"),
            named("spotlessKotlinGradleCheck"),
        )
    }

    register("lintFormat") { // runs "spotlessApply"
        group = "verification"
        description = "Format all code using configured formatters. Runs 'spotlessApply' "
        dependsOn(named("spotlessApply"))
    }

    register("ktlintFormat") { // runs "spotlessKotlinApply" and "spotlessKotlinGradleApply"
        group = "verification"
        description = "Format Kotlin code. Runs 'spotlessKotlinApply' and 'spotlessKotlinGradleApply'"
        dependsOn(
            named("spotlessKotlinApply"),
            named("spotlessKotlinGradleApply"),
        )
    }

    named("check") { dependsOn(named("detekt")) }
}

internal val Project.isKotlinJvm: Boolean
    get() = pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")
