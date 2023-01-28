import org.gradle.api.internal.FeaturePreviews.Feature.TYPESAFE_PROJECT_ACCESSORS
import org.gradle.api.internal.FeaturePreviews.Feature.VERSION_CATALOGS

enableFeaturePreview(TYPESAFE_PROJECT_ACCESSORS.name)
enableFeaturePreview(VERSION_CATALOGS.name)

rootProject.name = "klog"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

gradle.rootProject {
    val projectGroup: String by settings
    val projectVersion: String by settings

    allprojects {
        group = projectGroup
        version = projectVersion
        description = "a bliki written in kotlin"
    }
}
