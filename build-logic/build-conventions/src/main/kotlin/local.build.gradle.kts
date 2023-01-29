import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

val Project.isKotlinJvm: Boolean
    get() = pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")

val libs: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
fun VersionCatalog.version(alias: String): String =
    this.findVersion(alias).get().requiredVersion

val jvmVersion: String = libs.version("jvm-target")
val kotlinLanguageVersion: String = libs.version("kotlin-language-level")

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

tasks {
    withType<Test> {
        configureEach {
            maxParallelForks = 1
            useJUnitPlatform()
            testLogging {
                events = setOf(
                    TestLogEvent.FAILED,
                    TestLogEvent.STANDARD_ERROR,
                    TestLogEvent.STANDARD_OUT,
                    TestLogEvent.SKIPPED,
                )
                exceptionFormat = TestExceptionFormat.FULL
                showExceptions = true
                showCauses = true
                showStackTraces = true
            }
        }
    }

    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(jvmVersion))
            freeCompilerArgs.addAll("-progressive", "-version", "-Xjsr305=strict",)
            allWarningsAsErrors.set(true)
            languageVersion.set(KotlinVersion.fromVersion(kotlinLanguageVersion))
            apiVersion.set(KotlinVersion.fromVersion(kotlinLanguageVersion))
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.toVersion(jvmVersion)
    targetCompatibility = JavaVersion.toVersion(jvmVersion)
}
