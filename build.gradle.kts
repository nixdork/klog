@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)

    id("local.build")
    id("local.analysis")
}

repositories {
    mavenCentral()
}

dependencies {
    listOf(
        gradleKotlinDsl(),
        libs.kotlin.stdlib,
        libs.kotlin.logging.jvm,
        libs.java.validation.api,
        libs.jetbrains.annotations,
        libs.bundles.springboot,
        libs.bundles.spring,
        libs.bundles.serialize,
        libs.bundles.database,
    ).forEach {
        implementation(it)
    }

    listOf(
        libs.serpro.kotlin.faker,
        libs.spring.boot.test,
        libs.bundles.kotest,
        libs.bundles.testcontainers,
    ).forEach {
        testImplementation(it)
    }

    developmentOnly(libs.spring.boot.devtools)
    detektPlugins(libs.arturbosch.detekt.formatting)
}
