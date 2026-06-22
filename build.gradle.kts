plugins {
    kotlin("jvm") version "2.4.0"
}

group = "fmt.kotlin.advanced"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
    testImplementation("io.kotest:kotest-assertions-core-jvm:6.1.11")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
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
