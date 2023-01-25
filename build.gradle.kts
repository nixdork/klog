import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.adarshr.gradle.testlogger.theme.ThemeType
import java.util.*

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.0"
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.adarshr.test-logger") version "3.2.0"
    id("com.faire.gradle.analyze") version "1.0.9"
}

group = "org.nixdork"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("random.testing.seed", Random().nextInt())
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
}

testlogger {
    theme = ThemeType.STANDARD // project level
    showCauses = true
    showExceptions = true
    showFullStackTraces = false
    showPassed = true
    showSkipped = true
    showStackTraces = true
    showSummary = true
    slowThreshold = 5000
}
