import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.task.tree)
    id("local.build")
}

repositories {
    mavenCentral()
}

logger.lifecycle("> JDK toolchain version: ${java.toolchain.languageVersion.get()}")
logger.lifecycle("> Kotlin version: ${extensions.findByType<KotlinTopLevelExtension>()?.coreLibrariesVersion}")
