import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("com.faire.gradle.analyze")
    id("io.gitlab.arturbosch.detekt")
    id("com.diffplug.spotless")
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("${rootDir.path}/detekt.yml")
    source = files("**/kotlin/**")
}

spotless {
    kotlin {
        targetExclude("**/generated/**")
        ktlint().setUseExperimental(false)
    }
    kotlinGradle { ktlint().setUseExperimental(false) }
}

tasks {
    withType<Detekt> {
        configureEach {
            reports {
                html.required.set(true)
                xml.required.set(false)
                txt.required.set(false)
                sarif.required.set(false)
            }
        }
    }

//    register("lint") {
//        group = "verification"
//        description = "Lint all code using configured linters. Runs 'spotlessCheck' "
//        dependsOn(named("spotlessCheck"))
//    }
//
//    register("ktlint") { // runs "spotlessKotlinCheck" and "spotlessKotlinGradleCheck"
//        group = "verification"
//        description = "Lint Kotlin code. Runs 'spotlessKotlinCheck' and 'spotlessKotlinGradleCheck'"
//        dependsOn(
//            named("spotlessKotlinCheck"),
//            named("spotlessKotlinGradleCheck"),
//        )
//    }
//
//    register("lintFormat") { // runs "spotlessApply"
//        group = "verification"
//        description = "Format all code using configured formatters. Runs 'spotlessApply' "
//        dependsOn(named("spotlessApply"))
//    }
//
//    register("ktlintFormat") { // runs "spotlessKotlinApply" and "spotlessKotlinGradleApply"
//        group = "verification"
//        description = "Format Kotlin code. Runs 'spotlessKotlinApply' and 'spotlessKotlinGradleApply'"
//        dependsOn(
//            named("spotlessKotlinApply"),
//            named("spotlessKotlinGradleApply"),
//        )
//    }

    named("check") { dependsOn(named("detekt")) }
}
