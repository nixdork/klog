plugins {
    `kotlin-dsl`
}

dependencies {
    listOf(
        libs.kotlin.gradle,
        libs.arturbosch.detekt,
        libs.diffplug.spotless,
        libs.faire.gradle.analyze
    ).forEach {
        implementation(it)
    }
}
