@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.flyway.gradle)
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
        libs.flyway.core,
        libs.java.validation.api,
        libs.jetbrains.annotations,
        libs.spring.boot.autoconfigure,
        libs.bundles.spring,
        libs.bundles.serialize,
        libs.bundles.database,
    ).forEach {
        implementation(it)
    }

    implementation(libs.spring.boot) {
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "ch.qos.logback", module = "logback-core")
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "org.slf4j", module = "log4j-over-slf4j")
    }

    listOf(
        libs.serpro.kotlin.faker,
        libs.spring.boot.test,
        libs.bundles.kotest,
        libs.bundles.testcontainers,
    ).forEach {
        testImplementation(it)
    }

    annotationProcessor(libs.spring.boot.config.processor)
    developmentOnly(libs.spring.boot.devtools)
    detektPlugins(libs.arturbosch.detekt.formatting)
}

val appMainClassName = "org.nixdork.klog.KlogApplicationKt"
application {
    mainClass.set(appMainClassName)
}

tasks {
    bootRun.configure {
        systemProperty(
            "spring.profiles.active",
            System.getenv("SPRING_PROFILES_ACTIVE") ?: "development",
        )
    }
}

val postgresHost = System.getenv("DB_HOST") ?: "127.0.0.1"
val postgresPort = System.getenv("DB_PORT") ?: "5432"
flyway {
    url = "jdbc:postgresql://$postgresHost:$postgresPort/${rootProject.name}"
    driver = "org.postgresql.Driver"
    user = "postgres"
    password = "password1"
    schemas = arrayOf(rootProject.name)
}
