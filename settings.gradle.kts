plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "fmt-kotlin-advanced-tps"

include(
    "modules:core",
    "modules:ksp-variance-processor",
    "modules:annotation-variance",
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
