plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "fmt.kotlin.advanced"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.kotest.assertions)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}
