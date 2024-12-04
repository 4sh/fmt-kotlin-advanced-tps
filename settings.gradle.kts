plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "untitled"

include(
    "modules:core",
    "modules:ksp-variance-processor",
    "modules:annotation-variance",
)